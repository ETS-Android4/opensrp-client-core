package org.smartregister.view.fragment;

import static org.smartregister.AllConstants.SyncInfo.SYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.SYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.TASK_UNPROCESSED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.VALID_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.VALID_EVENTS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;
import org.smartregister.view.presenter.StatsFragmentPresenter;

import java.util.HashMap;
import java.util.Map;


public class StatsFragmentTest extends BaseUnitTest {

    private StatsFragment statsFragment;

    private StatsFragmentPresenter presenter;

    @Mock
    private Bundle savedInstanceState;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        statsFragment = Mockito.mock(StatsFragment.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void getInstanceReturnsNonNullFragmentInstance() {
        Assert.assertNotNull(StatsFragment.newInstance(Mockito.mock(Bundle.class)));
    }

    @Test
    public void onCreateInitializesPresenter() {
        FragmentManager mChildFragmentManager = Mockito.mock(FragmentManager.class);
        Whitebox.setInternalState(statsFragment, "mChildFragmentManager", mChildFragmentManager);
        statsFragment.onCreate(savedInstanceState);
        presenter = ReflectionHelpers.getField(statsFragment, "presenter");
        Assert.assertNotNull(presenter);
    }

    @Test
    public void onCreateViewInitsViewsAndReturnsCorrectView() {
        LayoutInflater inflater = Mockito.mock(LayoutInflater.class);
        ViewGroup container = Mockito.mock(ViewGroup.class);

        Button btnRefreshStats = Mockito.mock(Button.class);
        View rootView = Mockito.spy(Mockito.mock(View.class));
        statsFragment = Mockito.spy(statsFragment);

        presenter = Mockito.spy(Mockito.mock(StatsFragmentPresenter.class));
        ReflectionHelpers.setField(statsFragment, "presenter", presenter);

        Mockito.doReturn(btnRefreshStats).when(rootView).findViewById(R.id.refresh_button);
        Mockito.doReturn(rootView).when(inflater).inflate(ArgumentMatchers.anyInt(), ArgumentMatchers.any(ViewGroup.class), ArgumentMatchers.anyBoolean());

        View returnedView = statsFragment.onCreateView(inflater, container, savedInstanceState);
        Assert.assertNotNull(returnedView);
        Mockito.verify(presenter).fetchSyncInfo();
    }

    @Test
    public void refreshECSyncInfoUpdatesViews() {
        Map<String, Integer> syncInfoMap = new HashMap<>();
        syncInfoMap.put(SYNCED_EVENTS, 2);
        syncInfoMap.put(UNSYNCED_EVENTS, 3);
        syncInfoMap.put(TASK_UNPROCESSED_EVENTS, 4);
        syncInfoMap.put(SYNCED_CLIENTS, 5);
        syncInfoMap.put(UNSYNCED_CLIENTS, 6);
        syncInfoMap.put(VALID_EVENTS, 7);
        syncInfoMap.put(VALID_CLIENTS, 8);

        View rootView = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.fragment_stats, null);

        TextView tvSyncedEvents = rootView.findViewById(R.id.synced_events);
        TextView tvUnSyncedEvents = rootView.findViewById(R.id.unsynced_events);

        ReflectionHelpers.setField(statsFragment, "tvSyncedEvents", tvSyncedEvents);
        ReflectionHelpers.setField(statsFragment, "tvUnSyncedEvents", tvUnSyncedEvents);
        ReflectionHelpers.setField(statsFragment, "tvTaskUnprocessedEvents", rootView.findViewById(R.id.task_unprocessed_events));
        ReflectionHelpers.setField(statsFragment, "tvSyncedClient", rootView.findViewById(R.id.synced_clients));
        ReflectionHelpers.setField(statsFragment, "tvUnSyncedClients", rootView.findViewById(R.id.unsynced_clients));
        ReflectionHelpers.setField(statsFragment, "tvValidatedEvents", rootView.findViewById(R.id.validated_events));
        ReflectionHelpers.setField(statsFragment, "tvValidatedClients", rootView.findViewById(R.id.validated_clients));

        statsFragment.refreshECSyncInfo(syncInfoMap);
        Assert.assertEquals("2", tvSyncedEvents.getText());
        Assert.assertEquals("3", tvUnSyncedEvents.getText());
    }

}
