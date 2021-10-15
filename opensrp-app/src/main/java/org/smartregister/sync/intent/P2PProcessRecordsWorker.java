package org.smartregister.sync.intent;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

import java.net.SocketException;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 10/05/2019
 */

public class P2PProcessRecordsWorker extends BaseSyncIntentWorker {

    public P2PProcessRecordsWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    protected void onRunWork() throws SocketException {
        try {
            AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

            if (allSharedPreferences.isPeerToPeerUnprocessedEvents()) {
                CoreLibrary.getInstance().setPeerToPeerProcessing(true);

                long eventsMaxRowId = allSharedPreferences.getLastPeerToPeerSyncProcessedEvent();
                EventClientRepository eventClientRepository = CoreLibrary.getInstance().context().getEventClientRepository();

                while (eventsMaxRowId > -1) {
                    EventClientQueryResult eventClientQueryResult = eventClientRepository.fetchEventClientsByRowId(eventsMaxRowId);
                    List<EventClient> eventClientList = eventClientQueryResult.getEventClientList();

                    if (eventClientList.size() > 0) {
                        try {
                            DrishtiApplication.getInstance().getClientProcessor().processClient(eventClientList);
                            int tableMaxRowId = eventClientRepository.getMaxRowId(EventClientRepository.Table.event);

                            if (tableMaxRowId == eventClientQueryResult.maxRowId) {
                                eventsMaxRowId = -1;
                                allSharedPreferences.resetLastPeerToPeerSyncProcessedEvent();
                            } else {
                                eventsMaxRowId = eventClientQueryResult.maxRowId;
                                allSharedPreferences.setLastPeerToPeerSyncProcessedEvent(eventClientQueryResult.maxRowId);
                            }

                            // Profile images do not have a foreign key to the clients and can therefore be saved during the sync.
                            // They also do not take long to save and therefore happen during sync
                            Timber.i("Finished processing %s EventClients", String.valueOf(eventClientList.size()));
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    } else {
                        allSharedPreferences.resetLastPeerToPeerSyncProcessedEvent();
                        break;
                    }

                }

                sendSyncStatusBroadcastMessage(FetchStatus.fetched);
            }
        }finally {
            // This ensure that even if the `onHandleIntent` is closed prematurely, we remove the Snackbar
            if (CoreLibrary.getInstance().isPeerToPeerProcessing()) {
                CoreLibrary.getInstance().setPeerToPeerProcessing(false);
            }
        }
    }

    @VisibleForTesting
    protected void sendSyncStatusBroadcastMessage(FetchStatus fetchStatus) {
        CoreLibrary.getInstance().context().applicationContext().sendBroadcast(Utils.completeSync(fetchStatus));
    }

    public static class EventClientQueryResult {

        private List<EventClient> eventClientList;
        private int maxRowId;

        public EventClientQueryResult(int maxRowId, @NonNull List<EventClient> eventClients) {
            this.maxRowId = maxRowId;
            this.eventClientList = eventClients;
        }

        public List<EventClient> getEventClientList() {
            return eventClientList;
        }

        public void setEventClientList(List<EventClient> eventClientList) {
            this.eventClientList = eventClientList;
        }

        public int getMaxRowId() {
            return maxRowId;
        }

        public void setMaxRowId(int maxRowId) {
            this.maxRowId = maxRowId;
        }
    }
}
