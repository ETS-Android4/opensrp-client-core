package org.smartregister.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.NativeFormFieldProcessor;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.Location;
import org.smartregister.domain.Task;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.KEY;
import static org.smartregister.util.JsonFormUtils.OPENMRS_ENTITY;
import static org.smartregister.util.JsonFormUtils.OPENMRS_ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.OPENMRS_ENTITY_PARENT;
import static org.smartregister.util.JsonFormUtils.VALUE;

public class NativeFormProcessorTest extends BaseUnitTest {

    @Captor
    private ArgumentCaptor<JSONObject> argumentCaptor;

    @Test
    public void testProcessingAndSavingClient() throws Exception {
        ECSyncHelper ecSyncHelper = Mockito.mock(ECSyncHelper.class);

        JSONObject client = new JSONObject("{\"_id\": \"9b67a82d-dac7-40c0-85aa-e5976339a6b6\", \"_rev\": \"v1\", \"type\": \"Client\", \"gender\": \"Male\", \"lastName\": \"Family\", \"addresses\": [{\"addressType\": \"\", \"cityVillage\": \"Tha Luang\"}], \"birthdate\": \"1970-01-01T04:00:00.000+02:00\", \"firstName\": \"Khumpai\", \"attributes\": {\"residence\": \"da765947-5e4d-49f7-9eb8-2d2d00681f65\"}, \"dateCreated\": \"2019-05-12T16:22:31.023+02:00\", \"identifiers\": {\"opensrp_id\": \"11096120_family\"}, \"baseEntityId\": \"71ad460c-bf76-414e-9be1-0d1b2cb1bce8\", \"relationships\": {\"family_head\": [\"7d97182f-d623-4553-8651-5a29d2fe3f0b\"], \"primary_caregiver\": [\"7d97182f-d623-4553-8651-5a29d2fe3f0b\"]}, \"serverVersion\": 1557670950986, \"birthdateApprox\": false, \"deathdateApprox\": false, \"clientDatabaseVersion\": 2, \"clientApplicationVersion\": 2}");
        Mockito.doReturn(client).when(ecSyncHelper).getClient(Mockito.any());

        String jsonString = "{\"count\": \"1\", \"step1\": {\"title\": \"Add Student\", \"fields\": [{\"key\": \"unique_id\", \"hint\": \"ID\", \"type\": \"hidden\", \"read_only\": true, \"v_required\": {\"err\": \"Please enter the ID\", \"value\": \"true\"}, \"openmrs_entity\": \"person_identifier\", \"openmrs_entity_id\": \"opensrp_id\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaNoNationalId\", \"type\": \"check_box\", \"label\": \"\", \"options\": [{\"key\": \"noNationalID\", \"text\": \"Child does not have a national ID\", \"value\": \"false\", \"text_size\": \"18px\"}], \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"has_no_id\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaNationalId\", \"hint\": \"National ID\", \"type\": \"edit_text\", \"relevance\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-relevance.yml\"}}}, \"text_size\": \"8sp\", \"v_numeric\": {\"err\": \"Must be a number.\", \"value\": \"true\"}, \"v_required\": {\"err\": \"Please enter the ID\", \"value\": \"true\"}, \"openmrs_entity\": \"person_identifier\", \"openmrs_entity_id\": \"national_id\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaRevealId\", \"hint\": \"Reveal ID\", \"type\": \"hidden\", \"text_size\": \"8sp\", \"calculation\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-calculation.yml\"}}}, \"openmrs_entity\": \"person_identifier\", \"openmrs_entity_id\": \"reveal_id\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaGivenName\", \"hint\": \"Child's given name\", \"type\": \"edit_text\", \"edit_type\": \"name\", \"text_size\": \"8sp\", \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person\", \"openmrs_entity_id\": \"first_name\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaSurname\", \"hint\": \"Child's surname\", \"type\": \"edit_text\", \"edit_type\": \"name\", \"text_size\": \"8sp\", \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person\", \"openmrs_entity_id\": \"last_name\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaSex\", \"type\": \"native_radio\", \"label\": \"Child's allocated sex at birth\", \"options\": [{\"key\": \"Male\", \"text\": \"Male\"}, {\"key\": \"Female\", \"text\": \"Female\"}], \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person\", \"openmrs_entity_id\": \"gender\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaDob\", \"hint\": \"Child's date of birth\", \"type\": \"date_picker\", \"duration\": {\"label\": \"Age\"}, \"expanded\": false, \"max_date\": \"today-5y\", \"min_date\": \"today-120y\", \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person\", \"openmrs_entity_id\": \"birthdate\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaDobUnk\", \"type\": \"check_box\", \"label\": \"\", \"options\": [{\"key\": \"sactaDobUnk\", \"text\": \"Date of birth estimated\", \"value\": \"false\", \"text_size\": \"18px\"}], \"openmrs_entity\": \"person\", \"openmrs_entity_id\": \"birthdateApprox\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaAge\", \"hint\": \"Child's age\", \"type\": \"edit_text\", \"v_max\": {\"err\": \"Age must be equal or less than 120\", \"value\": \"120\"}, \"v_min\": {\"err\": \"Age must be equal or greater than 5\", \"value\": \"5\"}, \"read_only\": true, \"text_size\": \"8sp\", \"v_required\": {\"err\": \"Please enter the age\", \"value\": true}, \"calculation\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-calculation.yml\"}}}, \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"age_entered\", \"v_numeric_integer\": {\"err\": \"Please enter a number\", \"value\": \"true\"}, \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaAgeCat\", \"hint\": \"Age category\", \"type\": \"edit_text\", \"read_only\": true, \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"calculation\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-calculation.yml\"}}}, \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"age_category\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaCurrEnroll\", \"type\": \"native_radio\", \"label\": \"Is this child currently enrolled in school?\", \"options\": [{\"key\": \"Yes\", \"text\": \"Yes\"}, {\"key\": \"No\", \"text\": \"No\"}], \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"school_enrolled\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaCurrSchName\", \"hint\": \"Name of school attending, if from another school\", \"type\": \"edit_text\", \"edit_type\": \"name\", \"relevance\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-relevance.yml\"}}}, \"text_size\": \"8sp\", \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"school_name\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaGrade\", \"type\": \"native_radio\", \"label\": \"School grade\", \"options\": [{\"key\": \"Grade 1\", \"text\": \"Grade 1\"}, {\"key\": \"Grade 2\", \"text\": \"Grade 2\"}, {\"key\": \"Grade 3\", \"text\": \"Grade 3\"}, {\"key\": \"Grade 4\", \"text\": \"Grade 4\"}, {\"key\": \"Grade 5\", \"text\": \"Grade 5\"}, {\"key\": \"Grade 6\", \"text\": \"Grade 6\"}, {\"key\": \"Grade 7\", \"text\": \"Grade 7\"}, {\"key\": \"Form 1\", \"text\": \"Form 1\"}, {\"key\": \"Form 2\", \"text\": \"Form 2\"}, {\"key\": \"Form 3\", \"text\": \"Form 3\"}, {\"key\": \"Form 4\", \"text\": \"Form 4\"}, {\"key\": \"Form 5\", \"text\": \"Form 5\"}], \"relevance\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-relevance.yml\"}}}, \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"grade\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaClass\", \"hint\": \"Grade class (e.g. 1A, 1B, 1C/D)\", \"type\": \"edit_text\", \"edit_type\": \"name\", \"relevance\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-relevance.yml\"}}}, \"text_size\": \"8sp\", \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"grade_class\", \"openmrs_entity_parent\": \"\"}]}, \"metadata\": {\"end\": {\"openmrs_entity\": \"concept\", \"openmrs_data_type\": \"end\", \"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", \"openmrs_entity_parent\": \"\"}, \"start\": {\"openmrs_entity\": \"concept\", \"openmrs_data_type\": \"start\", \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", \"openmrs_entity_parent\": \"\"}, \"today\": {\"openmrs_entity\": \"encounter\", \"openmrs_entity_id\": \"encounter_date\", \"openmrs_entity_parent\": \"\"}, \"look_up\": {\"value\": \"\", \"entity_id\": \"\"}, \"deviceid\": {\"openmrs_entity\": \"concept\", \"openmrs_data_type\": \"deviceid\", \"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", \"openmrs_entity_parent\": \"\"}, \"simserial\": {\"openmrs_entity\": \"concept\", \"openmrs_data_type\": \"simserial\", \"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", \"openmrs_entity_parent\": \"\"}, \"phonenumber\": {\"openmrs_entity\": \"concept\", \"openmrs_data_type\": \"phonenumber\", \"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", \"openmrs_entity_parent\": \"\"}, \"subscriberid\": {\"openmrs_entity\": \"concept\", \"openmrs_data_type\": \"subscriberid\", \"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", \"openmrs_entity_parent\": \"\"}, \"encounter_location\": \"\"}, \"entity_id\": \"\", \"baseEntityId\": \"12345\", \"relational_id\": \"\", \"encounter_type\": \"Child Registration\"}";

        NativeFormProcessor processor = FormProcessorFactoryHelper.createInstance(jsonString);
        processor = Mockito.spy(processor);
        Mockito.doReturn(ecSyncHelper).when(processor).getSyncHelper();


        Task task = new Task();
        task.setIdentifier("taskid");
        task.setBusinessStatus("done");
        task.setStatus(Task.TaskStatus.COMPLETED);

        String entityId = "entityId";
        Location operationalArea = new Location();
        operationalArea.setServerVersion(0L);

        processor.withBindType("child")
                .withEncounterType("Child Registration")
                .withFormSubmissionId("formSubmissionId")
                .withEntityId(entityId)
                .tagLocationData(operationalArea)
                .tagTaskDetails(task)
                .tagEventMetadata()

                // create and save client
                .hasClient(true)
                .saveClient(_client -> _client.setMiddleName("Family"))
                .mergeAndSaveClient()

                // create and save event to db
                .saveEvent()

                // execute client processing
                .clientProcessForm();

        Mockito.verify(ecSyncHelper, Mockito.times(2)).addClient(Mockito.eq(entityId), Mockito.any());
        Mockito.verify(ecSyncHelper).addEvent(Mockito.eq(entityId), Mockito.any());

        Assert.assertEquals(processor.getFieldValue("sactaCurrEnroll"), "");
    }

    private String getJsonString() {
        return "{\n" +
                "    \"count\": \"1\",\n" +
                "    \"encounter_type\": \"OPD_Treatment\",\n" +
                "    \"entity_id\": \"\",\n" +
                "    \"metadata\": {\n" +
                "        \"start\": {\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"concept\",\n" +
                "            \"openmrs_data_type\": \"start\",\n" +
                "            \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "            \"value\": \"2021-07-27 23:28:15\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"step1\": {\n" +
                "        \"title\": \"Treatment\",\n" +
                "        \"fields\": [\n" +
                "            {\n" +
                "                \"key\": \"treatment_type_specify\",\n" +
                "                \"openmrs_entity_parent\": \"\",\n" +
                "                \"openmrs_entity\": \"\",\n" +
                "                \"openmrs_entity_id\": \"\",\n" +
                "                \"hint\": \"Specify any other treatment type\",\n" +
                "                \"type\": \"edit_text\",\n" +
                "                \"relevance\": {\n" +
                "                    \"rules-engine\": {\n" +
                "                        \"ex-rules\": {\n" +
                "                            \"rules-file\": \"opd\\/opd_treatment_relevance_rules.yml\"\n" +
                "                        }\n" +
                "                    }\n" +
                "                },\n" +
                "                \"is_visible\": false\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\": \"medicine\",\n" +
                "                \"openmrs_entity_parent\": \"\",\n" +
                "                \"openmrs_entity\": \"\",\n" +
                "                \"openmrs_entity_id\": \"\",\n" +
                "                \"sortClass\": \"org.smartregister.opd.comparator.MultiSelectListAlphabetTextComparator\",\n" +
                "                \"sort\": true,\n" +
                "                \"groupings\": \"[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z]\",\n" +
                "                \"source\": \"csv\",\n" +
                "                \"repositoryClass\": \"org.smartregister.giz.widget.GizOpdMedicineMultiSelectListRepository\",\n" +
                "                \"type\": \"multi_select_drug_picker\",\n" +
                "                \"buttonText\": \"+ Add treatment\\/medicine\",\n" +
                "                \"dialogTitle\": \"Add treatment\\/medicine\",\n" +
                "                \"searchHint\": \"Type treatment\\/medicine name\",\n" +
                "                \"relevance\": {\n" +
                "                    \"rules-engine\": {\n" +
                "                        \"ex-rules\": {\n" +
                "                            \"rules-file\": \"opd\\/opd_treatment_relevance_rules.yml\"\n" +
                "                        }\n" +
                "                    }\n" +
                "                },\n" +
                "                \"step\": \"step1\",\n" +
                "                \"is-rule-check\": true,\n" +
                "                \"is_visible\": true,\n" +
                "                \"value\": \"[{\\\"key\\\":\\\"AA004200\\\",\\\"text\\\":\\\"Amoxycillin 250mg dispersible tablets\\\",\\\"openmrs_entity\\\":\\\"\\\",\\\"openmrs_entity_id\\\":\\\"AA004200\\\",\\\"openmrs_entity_parent\\\":\\\"\\\",\\\"property\\\":{\\\"pack_size\\\":null,\\\"product_code\\\":\\\"AA004200\\\",\\\"dispensing_unit\\\":\\\"Tablet\\\",\\\"meta\\\":{\\\"duration\\\":\\\"1\\\",\\\"dosage\\\":\\\"1\\\",\\\"frequency\\\":\\\"1\\\",\\\"info\\\":\\\"Dose: 1, Duration: 1, Frequency: 1\\\"}}}]\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\": \"special_instructions\",\n" +
                "                \"openmrs_entity_parent\": \"\",\n" +
                "                \"openmrs_entity\": \"\",\n" +
                "                \"openmrs_entity_id\": \"\",\n" +
                "                \"hint\": \"Special Instructions\",\n" +
                "                \"type\": \"edit_text\",\n" +
                "                \"value\": \"none\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\": \"medicine_object\",\n" +
                "                \"openmrs_entity_parent\": \"\",\n" +
                "                \"openmrs_entity\": \"\",\n" +
                "                \"openmrs_entity_id\": \"\",\n" +
                "                \"type\": \"hidden\",\n" +
                "                \"calculation\": {\n" +
                "                    \"rules-engine\": {\n" +
                "                        \"ex-rules\": {\n" +
                "                            \"rules-file\": \"opd\\/opd_treatment_calculation.yml\"\n" +
                "                        }\n" +
                "                    }\n" +
                "                },\n" +
                "                \"value\": \"[{\\\"key\\\":\\\"AA004200\\\",\\\"text\\\":\\\"Amoxycillin 250mg dispersible tablets\\\",\\\"openmrs_entity\\\":\\\"\\\",\\\"openmrs_entity_id\\\":\\\"AA004200\\\",\\\"openmrs_entity_parent\\\":\\\"\\\",\\\"property\\\":{\\\"pack_size\\\":null,\\\"product_code\\\":\\\"AA004200\\\",\\\"dispensing_unit\\\":\\\"Tablet\\\",\\\"meta\\\":{\\\"duration\\\":\\\"1\\\",\\\"dosage\\\":\\\"1\\\",\\\"frequency\\\":\\\"1\\\",\\\"info\\\":\\\"Dose: 1, Duration: 1, Frequency: 1\\\"}}}]\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\": \"visit_id\",\n" +
                "                \"openmrs_entity_parent\": \"\",\n" +
                "                \"openmrs_entity\": \"\",\n" +
                "                \"openmrs_entity_id\": \"\",\n" +
                "                \"type\": \"hidden\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"baseEntityId\": \"xxxxxxxx-xxxx-xxxx-xxxx-xxxx0000xxxx\"\n" +
                "}";
    }

    @Test
    public void testProcessingAndSavingClientWithCustomNativeFormField() throws Exception {
        ECSyncHelper ecSyncHelper = Mockito.mock(ECSyncHelper.class);
        String jsonString = getJsonString();


        NativeFormProcessor processor = FormProcessorFactoryHelper.createInstance(jsonString);
        processor = Mockito.spy(processor);
        Mockito.doReturn(ecSyncHelper).when(processor).getSyncHelper();

        String entityId = "entityId";

        processor.withBindType("child")
                .withEncounterType("Child Registration")
                .withFormSubmissionId("formSubmissionId")
                .withEntityId(entityId)
                .withFieldProcessors(getFieldProcessorMap())

                .tagEventMetadata()

                // create and save event to db
                .saveEvent()

                // execute client processing
                .clientProcessForm();

        Mockito.verify(ecSyncHelper).addEvent(Mockito.eq(entityId), argumentCaptor.capture());

        // check processed event

        Event event = Whitebox.getInternalState(processor, "_event");
        List<Obs> obs = event.getObs();

        Set<Object> values = new HashSet<>();

        for (Obs o : obs) {
            values.addAll(o.getValues());
        }

        Assert.assertTrue(values.contains("AA004200"));
    }

    private Map<String, NativeFormFieldProcessor> getFieldProcessorMap() {
        Map<String, NativeFormFieldProcessor> fieldProcessorMap = new HashMap<>();
        fieldProcessorMap.put("multi_select_drug_picker", (event, jsonObject1) -> {
            JSONArray valuesJsonArray;
            try {
                valuesJsonArray = new JSONArray(jsonObject1.optString(VALUE));
                for (int i = 0; i < valuesJsonArray.length(); i++) {
                    JSONObject jsonValObject = valuesJsonArray.optJSONObject(i);
                    String fieldType = jsonValObject.optString(OPENMRS_ENTITY);
                    String fieldCode = jsonObject1.optString(OPENMRS_ENTITY_ID);
                    String parentCode = jsonObject1.optString(OPENMRS_ENTITY_PARENT);
                    String value = jsonValObject.optString(OPENMRS_ENTITY_ID);
                    String humanReadableValues = jsonValObject.optString(AllConstants.TEXT);
                    String formSubmissionField = jsonObject1.optString(KEY);
                    event.addObs(new Obs(fieldType, AllConstants.TEXT, fieldCode, parentCode, Collections.singletonList(value),
                            Collections.singletonList(humanReadableValues), "", formSubmissionField));
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        });
        return fieldProcessorMap;
    }

    @Test
    public void testPopulateValues() throws JSONException {
        String jsonString = "{\"count\": \"1\", \"step1\": {\"title\": \"Add Student\", \"fields\": [{\"key\": \"unique_id\", \"hint\": \"ID\", \"type\": \"hidden\", \"read_only\": true, \"v_required\": {\"err\": \"Please enter the ID\", \"value\": \"true\"}, \"openmrs_entity\": \"person_identifier\", \"openmrs_entity_id\": \"opensrp_id\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaNoNationalId\", \"type\": \"check_box\", \"label\": \"\", \"options\": [{\"key\": \"noNationalID\", \"text\": \"Child does not have a national ID\", \"value\": \"false\", \"text_size\": \"18px\"}], \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"has_no_id\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaNationalId\", \"hint\": \"National ID\", \"type\": \"edit_text\", \"relevance\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-relevance.yml\"}}}, \"text_size\": \"8sp\", \"v_numeric\": {\"err\": \"Must be a number.\", \"value\": \"true\"}, \"v_required\": {\"err\": \"Please enter the ID\", \"value\": \"true\"}, \"openmrs_entity\": \"person_identifier\", \"openmrs_entity_id\": \"national_id\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaRevealId\", \"hint\": \"Reveal ID\", \"type\": \"hidden\", \"text_size\": \"8sp\", \"calculation\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-calculation.yml\"}}}, \"openmrs_entity\": \"person_identifier\", \"openmrs_entity_id\": \"reveal_id\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaGivenName\", \"hint\": \"Child's given name\", \"type\": \"edit_text\", \"edit_type\": \"name\", \"text_size\": \"8sp\", \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person\", \"openmrs_entity_id\": \"first_name\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaSurname\", \"hint\": \"Child's surname\", \"type\": \"edit_text\", \"edit_type\": \"name\", \"text_size\": \"8sp\", \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person\", \"openmrs_entity_id\": \"last_name\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaSex\", \"type\": \"native_radio\", \"label\": \"Child's allocated sex at birth\", \"options\": [{\"key\": \"Male\", \"text\": \"Male\"}, {\"key\": \"Female\", \"text\": \"Female\"}], \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person\", \"openmrs_entity_id\": \"gender\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaDob\", \"hint\": \"Child's date of birth\", \"type\": \"date_picker\", \"duration\": {\"label\": \"Age\"}, \"expanded\": false, \"max_date\": \"today-5y\", \"min_date\": \"today-120y\", \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person\", \"openmrs_entity_id\": \"birthdate\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaDobUnk\", \"type\": \"check_box\", \"label\": \"\", \"options\": [{\"key\": \"sactaDobUnk\", \"text\": \"Date of birth estimated\", \"value\": \"false\", \"text_size\": \"18px\"}], \"openmrs_entity\": \"person\", \"openmrs_entity_id\": \"birthdateApprox\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaAge\", \"hint\": \"Child's age\", \"type\": \"edit_text\", \"v_max\": {\"err\": \"Age must be equal or less than 120\", \"value\": \"120\"}, \"v_min\": {\"err\": \"Age must be equal or greater than 5\", \"value\": \"5\"}, \"read_only\": true, \"text_size\": \"8sp\", \"v_required\": {\"err\": \"Please enter the age\", \"value\": true}, \"calculation\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-calculation.yml\"}}}, \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"age_entered\", \"v_numeric_integer\": {\"err\": \"Please enter a number\", \"value\": \"true\"}, \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaAgeCat\", \"hint\": \"Age category\", \"type\": \"edit_text\", \"read_only\": true, \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"calculation\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-calculation.yml\"}}}, \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"age_category\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaCurrEnroll\", \"type\": \"native_radio\", \"label\": \"Is this child currently enrolled in school?\", \"options\": [{\"key\": \"Yes\", \"text\": \"Yes\"}, {\"key\": \"No\", \"text\": \"No\"}], \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"school_enrolled\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaCurrSchName\", \"hint\": \"Name of school attending, if from another school\", \"type\": \"edit_text\", \"edit_type\": \"name\", \"relevance\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-relevance.yml\"}}}, \"text_size\": \"8sp\", \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"school_name\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaGrade\", \"type\": \"native_radio\", \"label\": \"School grade\", \"options\": [{\"key\": \"Grade 1\", \"text\": \"Grade 1\"}, {\"key\": \"Grade 2\", \"text\": \"Grade 2\"}, {\"key\": \"Grade 3\", \"text\": \"Grade 3\"}, {\"key\": \"Grade 4\", \"text\": \"Grade 4\"}, {\"key\": \"Grade 5\", \"text\": \"Grade 5\"}, {\"key\": \"Grade 6\", \"text\": \"Grade 6\"}, {\"key\": \"Grade 7\", \"text\": \"Grade 7\"}, {\"key\": \"Form 1\", \"text\": \"Form 1\"}, {\"key\": \"Form 2\", \"text\": \"Form 2\"}, {\"key\": \"Form 3\", \"text\": \"Form 3\"}, {\"key\": \"Form 4\", \"text\": \"Form 4\"}, {\"key\": \"Form 5\", \"text\": \"Form 5\"}], \"relevance\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-relevance.yml\"}}}, \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"grade\", \"openmrs_entity_parent\": \"\"}, {\"key\": \"sactaClass\", \"hint\": \"Grade class (e.g. 1A, 1B, 1C/D)\", \"type\": \"edit_text\", \"edit_type\": \"name\", \"relevance\": {\"rules-engine\": {\"ex-rules\": {\"rules-file\": \"ntd-child-registration-relevance.yml\"}}}, \"text_size\": \"8sp\", \"v_required\": {\"err\": \"Required field\", \"value\": \"true\"}, \"openmrs_entity\": \"person_attribute\", \"openmrs_entity_id\": \"grade_class\", \"openmrs_entity_parent\": \"\"}]}, \"metadata\": {\"end\": {\"openmrs_entity\": \"concept\", \"openmrs_data_type\": \"end\", \"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", \"openmrs_entity_parent\": \"\"}, \"start\": {\"openmrs_entity\": \"concept\", \"openmrs_data_type\": \"start\", \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", \"openmrs_entity_parent\": \"\"}, \"today\": {\"openmrs_entity\": \"encounter\", \"openmrs_entity_id\": \"encounter_date\", \"openmrs_entity_parent\": \"\"}, \"look_up\": {\"value\": \"\", \"entity_id\": \"\"}, \"deviceid\": {\"openmrs_entity\": \"concept\", \"openmrs_data_type\": \"deviceid\", \"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", \"openmrs_entity_parent\": \"\"}, \"simserial\": {\"openmrs_entity\": \"concept\", \"openmrs_data_type\": \"simserial\", \"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", \"openmrs_entity_parent\": \"\"}, \"phonenumber\": {\"openmrs_entity\": \"concept\", \"openmrs_data_type\": \"phonenumber\", \"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", \"openmrs_entity_parent\": \"\"}, \"subscriberid\": {\"openmrs_entity\": \"concept\", \"openmrs_data_type\": \"subscriberid\", \"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", \"openmrs_entity_parent\": \"\"}, \"encounter_location\": \"\"}, \"entity_id\": \"\", \"baseEntityId\": \"12345\", \"relational_id\": \"\", \"encounter_type\": \"Child Registration\"}";
        JSONObject jsonObject = new JSONObject(jsonString);

        // get key for values
        Map<String, Object> values = new HashMap<>();
        values.put("unique_id", "3456");
        values.put("sactaNationalId", "678678678");
        values.put("sactaRevealId", "5657567567");
        values.put("sactaGivenName", "Ronald");
        values.put("sactaSurname", "Developer");
        values.put("sactaSex", "Male");
        values.put("sactaDob", "2000-01-01");
        values.put("sactaDobUnk", "false");
        values.put("sactaCurrEnroll", "Yes");
        values.put("sactaCurrSchName", "Nairobi School");
        values.put("sactaGrade", "Form 1");
        values.put("sactaClass", "Form");


        FormProcessorFactoryHelper.createInstance(jsonObject)
                .populateValues(values);


        NativeFormProcessor processor = FormProcessorFactoryHelper.createInstance(jsonObject.toString());
        processor = Mockito.spy(processor);

        UniqueIdRepository uniqueIdRepository = Mockito.mock(UniqueIdRepository.class);
        Mockito.doReturn(uniqueIdRepository).when(processor).getUniqueIdRepository();

        processor.closeRegistrationID("unique_id");
        Mockito.verify(uniqueIdRepository).close(Mockito.anyString());

        String expected = "{\"count\":\"1\",\"step1\":{\"title\":\"Add Student\",\"fields\":[{\"key\":\"unique_id\",\"hint\":\"ID\",\"type\":\"hidden\",\"read_only\":true,\"v_required\":{\"err\":\"Please enter the ID\",\"value\":\"true\"},\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"opensrp_id\",\"openmrs_entity_parent\":\"\",\"value\":\"3456\"},{\"key\":\"sactaNoNationalId\",\"type\":\"check_box\",\"label\":\"\",\"options\":[{\"key\":\"noNationalID\",\"text\":\"Child does not have a national ID\",\"value\":\"false\",\"text_size\":\"18px\"}],\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"has_no_id\",\"openmrs_entity_parent\":\"\"},{\"key\":\"sactaNationalId\",\"hint\":\"National ID\",\"type\":\"edit_text\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ntd-child-registration-relevance.yml\"}}},\"text_size\":\"8sp\",\"v_numeric\":{\"err\":\"Must be a number.\",\"value\":\"true\"},\"v_required\":{\"err\":\"Please enter the ID\",\"value\":\"true\"},\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"national_id\",\"openmrs_entity_parent\":\"\",\"value\":\"678678678\"},{\"key\":\"sactaRevealId\",\"hint\":\"Reveal ID\",\"type\":\"hidden\",\"text_size\":\"8sp\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ntd-child-registration-calculation.yml\"}}},\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"reveal_id\",\"openmrs_entity_parent\":\"\",\"value\":\"5657567567\"},{\"key\":\"sactaGivenName\",\"hint\":\"Child's given name\",\"type\":\"edit_text\",\"edit_type\":\"name\",\"text_size\":\"8sp\",\"v_required\":{\"err\":\"Required field\",\"value\":\"true\"},\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"openmrs_entity_parent\":\"\",\"value\":\"Ronald\"},{\"key\":\"sactaSurname\",\"hint\":\"Child's surname\",\"type\":\"edit_text\",\"edit_type\":\"name\",\"text_size\":\"8sp\",\"v_required\":{\"err\":\"Required field\",\"value\":\"true\"},\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"openmrs_entity_parent\":\"\",\"value\":\"Developer\"},{\"key\":\"sactaSex\",\"type\":\"native_radio\",\"label\":\"Child's allocated sex at birth\",\"options\":[{\"key\":\"Male\",\"text\":\"Male\"},{\"key\":\"Female\",\"text\":\"Female\"}],\"v_required\":{\"err\":\"Required field\",\"value\":\"true\"},\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"openmrs_entity_parent\":\"\",\"value\":\"Male\"},{\"key\":\"sactaDob\",\"hint\":\"Child's date of birth\",\"type\":\"date_picker\",\"duration\":{\"label\":\"Age\"},\"expanded\":false,\"max_date\":\"today-5y\",\"min_date\":\"today-120y\",\"v_required\":{\"err\":\"Required field\",\"value\":\"true\"},\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"openmrs_entity_parent\":\"\",\"value\":\"2000-01-01\"},{\"key\":\"sactaDobUnk\",\"type\":\"check_box\",\"label\":\"\",\"options\":[{\"key\":\"sactaDobUnk\",\"text\":\"Date of birth estimated\",\"value\":\"false\",\"text_size\":\"18px\"}],\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdateApprox\",\"openmrs_entity_parent\":\"\",\"value\":[]},{\"key\":\"sactaAge\",\"hint\":\"Child's age\",\"type\":\"edit_text\",\"v_max\":{\"err\":\"Age must be equal or less than 120\",\"value\":\"120\"},\"v_min\":{\"err\":\"Age must be equal or greater than 5\",\"value\":\"5\"},\"read_only\":true,\"text_size\":\"8sp\",\"v_required\":{\"err\":\"Please enter the age\",\"value\":true},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ntd-child-registration-calculation.yml\"}}},\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"age_entered\",\"v_numeric_integer\":{\"err\":\"Please enter a number\",\"value\":\"true\"},\"openmrs_entity_parent\":\"\"},{\"key\":\"sactaAgeCat\",\"hint\":\"Age category\",\"type\":\"edit_text\",\"read_only\":true,\"v_required\":{\"err\":\"Required field\",\"value\":\"true\"},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ntd-child-registration-calculation.yml\"}}},\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"age_category\",\"openmrs_entity_parent\":\"\"},{\"key\":\"sactaCurrEnroll\",\"type\":\"native_radio\",\"label\":\"Is this child currently enrolled in school?\",\"options\":[{\"key\":\"Yes\",\"text\":\"Yes\"},{\"key\":\"No\",\"text\":\"No\"}],\"v_required\":{\"err\":\"Required field\",\"value\":\"true\"},\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"school_enrolled\",\"openmrs_entity_parent\":\"\",\"value\":\"Yes\"},{\"key\":\"sactaCurrSchName\",\"hint\":\"Name of school attending, if from another school\",\"type\":\"edit_text\",\"edit_type\":\"name\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ntd-child-registration-relevance.yml\"}}},\"text_size\":\"8sp\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"school_name\",\"openmrs_entity_parent\":\"\",\"value\":\"Nairobi School\"},{\"key\":\"sactaGrade\",\"type\":\"native_radio\",\"label\":\"School grade\",\"options\":[{\"key\":\"Grade 1\",\"text\":\"Grade 1\"},{\"key\":\"Grade 2\",\"text\":\"Grade 2\"},{\"key\":\"Grade 3\",\"text\":\"Grade 3\"},{\"key\":\"Grade 4\",\"text\":\"Grade 4\"},{\"key\":\"Grade 5\",\"text\":\"Grade 5\"},{\"key\":\"Grade 6\",\"text\":\"Grade 6\"},{\"key\":\"Grade 7\",\"text\":\"Grade 7\"},{\"key\":\"Form 1\",\"text\":\"Form 1\"},{\"key\":\"Form 2\",\"text\":\"Form 2\"},{\"key\":\"Form 3\",\"text\":\"Form 3\"},{\"key\":\"Form 4\",\"text\":\"Form 4\"},{\"key\":\"Form 5\",\"text\":\"Form 5\"}],\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ntd-child-registration-relevance.yml\"}}},\"v_required\":{\"err\":\"Required field\",\"value\":\"true\"},\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"grade\",\"openmrs_entity_parent\":\"\",\"value\":\"Form 1\"},{\"key\":\"sactaClass\",\"hint\":\"Grade class (e.g. 1A, 1B, 1C\\/D)\",\"type\":\"edit_text\",\"edit_type\":\"name\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"ntd-child-registration-relevance.yml\"}}},\"text_size\":\"8sp\",\"v_required\":{\"err\":\"Required field\",\"value\":\"true\"},\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"grade_class\",\"openmrs_entity_parent\":\"\",\"value\":\"Form\"}]},\"metadata\":{\"end\":{\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"},\"start\":{\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"},\"today\":{\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"openmrs_entity_parent\":\"\"},\"look_up\":{\"value\":\"\",\"entity_id\":\"\"},\"deviceid\":{\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"},\"simserial\":{\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"},\"phonenumber\":{\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"},\"subscriberid\":{\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"},\"encounter_location\":\"\"},\"entity_id\":\"\",\"baseEntityId\":\"12345\",\"relational_id\":\"\",\"encounter_type\":\"Child Registration\"}";
        Assert.assertEquals(expected, jsonObject.toString());

    }

    @Test
    public void testGetRepeatingGroupValues() throws JSONException {

        String jsonString = "{\n" +
                "        \"key\": \"tests_repeating_group\",\n" +
                "        \"type\": \"repeating_group\",\n" +
                "        \"reference_edit_text_hint\": \"# of tests\",\n" +
                "        \"repeating_group_label\": \"\",\n" +
                "        \"showGroupLabel\": false,\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"value\": [\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"The type of test conducted\",\n" +
                "            \"type\": \"spinner\",\n" +
                "            \"values\": [\n" +
                "              \"Pregnancy Test\",\n" +
                "              \"Ultra sound\",\n" +
                "              \"Malaria - Microscopy\",\n" +
                "              \"HIV test\",\n" +
                "              \"Syphilis Test - VDRL\",\n" +
                "              \"Hep B test\",\n" +
                "              \"Hep C test\",\n" +
                "              \"Blood Type test\",\n" +
                "              \"TB Screening\",\n" +
                "              \"Blood Glucose test (random plasma glucose test)\",\n" +
                "              \"Midstream urine Gram-staining\",\n" +
                "              \"Malaria - MRDT\",\n" +
                "              \"TB Gene Xpert\",\n" +
                "              \"TB smear microscopy\",\n" +
                "              \"TB urine LAM\",\n" +
                "              \"Urine dipstick\",\n" +
                "              \"Hemocue (haemoglobinometer)\",\n" +
                "              \"HIV Viral Load\",\n" +
                "              \"HIV EID\",\n" +
                "              \"HIV test - Rapid Test\",\n" +
                "              \"Other(specify)\"\n" +
                "            ],\n" +
                "            \"keys\": [\n" +
                "              \"pregnancy_test\",\n" +
                "              \"ultra_sound\",\n" +
                "              \"malaria_microscopy\",\n" +
                "              \"hiv_test\",\n" +
                "              \"syphilis_vdrl\",\n" +
                "              \"hep_b\",\n" +
                "              \"hep_c\",\n" +
                "              \"blood_type\",\n" +
                "              \"tb_screening\",\n" +
                "              \"blood_glucose_random_plasma_glucose_test\",\n" +
                "              \"midstream_urine_gram_staining\",\n" +
                "              \"malaria_mrdt\",\n" +
                "              \"tb_gene_xpert\",\n" +
                "              \"tb_smear_microscopy\",\n" +
                "              \"tb_urine_lam\",\n" +
                "              \"urine_dipstick\",\n" +
                "              \"hemocue_haemoglobinometer\",\n" +
                "              \"hiv_viral_load\",\n" +
                "              \"hiv_eid\",\n" +
                "              \"hiv_test_rapid\",\n" +
                "              \"other\"\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result_other\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"concept\",\n" +
                "            \"openmrs_entity_id\": \"160218AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "            \"type\": \"edit_text\",\n" +
                "            \"hint\": \"Specify the result of the test\",\n" +
                "            \"edit_type\": \"name\",\n" +
                "            \"v_required\": {\n" +
                "              \"value\": true,\n" +
                "              \"err\": \"Please specify the result of the test\"\n" +
                "            },\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"The result of the test conducted\",\n" +
                "            \"type\": \"spinner\",\n" +
                "            \"values\": [\n" +
                "              \"Positive\",\n" +
                "              \"Negative\",\n" +
                "              \"Inconclusive\"\n" +
                "            ],\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result_blood_type\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"The result of the test conducted\",\n" +
                "            \"type\": \"spinner\",\n" +
                "            \"values\": [\n" +
                "              \"A(Positive)\",\n" +
                "              \"B(Positive)\",\n" +
                "              \"AB(Positive)\",\n" +
                "              \"O(Positive)\",\n" +
                "              \"O(Negative)\",\n" +
                "              \"A(Negative)\",\n" +
                "              \"B(Negative)\",\n" +
                "              \"AB(Negative)\"\n" +
                "            ],\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result_tb_gene_xpert\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"Test Result\",\n" +
                "            \"type\": \"spinner\",\n" +
                "            \"values\": [\n" +
                "              \"MTB Detected & RR Not detected\",\n" +
                "              \"RR Detected & MTB Not Detected\",\n" +
                "              \"MTB Not Detected\",\n" +
                "              \"Error/Indeterminate\"\n" +
                "            ],\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result_tb_smear_microscopy\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"Test Result\",\n" +
                "            \"type\": \"spinner\",\n" +
                "            \"values\": [\n" +
                "              \"Negative\",\n" +
                "              \"Scanty\",\n" +
                "              \"1+\",\n" +
                "              \"2+\",\n" +
                "              \"3+\"\n" +
                "            ],\n" +
                "            \"keys\": [\n" +
                "              \"negative\",\n" +
                "              \"scanty\",\n" +
                "              \"1+\",\n" +
                "              \"2+\",\n" +
                "              \"3+\"\n" +
                "            ],\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result_urine_dipstick_nitrites\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"Urine dipstick result - nitrites\",\n" +
                "            \"type\": \"spinner\",\n" +
                "            \"values\": [\n" +
                "              \"None\",\n" +
                "              \"+\",\n" +
                "              \"++\",\n" +
                "              \"+++\",\n" +
                "              \"++++\"\n" +
                "            ],\n" +
                "            \"keys\": [\n" +
                "              \"none\",\n" +
                "              \"+\",\n" +
                "              \"++\",\n" +
                "              \"+++\",\n" +
                "              \"++++\"\n" +
                "            ],\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result_urine_dipstick_leukocytes\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"Urine dipstick result - leukocytes\",\n" +
                "            \"type\": \"spinner\",\n" +
                "            \"values\": [\n" +
                "              \"None\",\n" +
                "              \"+\",\n" +
                "              \"++\",\n" +
                "              \"+++\",\n" +
                "              \"++++\"\n" +
                "            ],\n" +
                "            \"keys\": [\n" +
                "              \"none\",\n" +
                "              \"+\",\n" +
                "              \"++\",\n" +
                "              \"+++\",\n" +
                "              \"++++\"\n" +
                "            ],\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result_urine_dipstick_protein\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"Urine dipstick result - protein\",\n" +
                "            \"type\": \"spinner\",\n" +
                "            \"values\": [\n" +
                "              \"None\",\n" +
                "              \"+\",\n" +
                "              \"++\",\n" +
                "              \"+++\",\n" +
                "              \"++++\"\n" +
                "            ],\n" +
                "            \"keys\": [\n" +
                "              \"none\",\n" +
                "              \"+\",\n" +
                "              \"++\",\n" +
                "              \"+++\",\n" +
                "              \"++++\"\n" +
                "            ],\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result_urine_dipstick_glucose\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"Urine dipstick result - glucose\",\n" +
                "            \"type\": \"spinner\",\n" +
                "            \"values\": [\n" +
                "              \"None\",\n" +
                "              \"+\",\n" +
                "              \"++\",\n" +
                "              \"+++\",\n" +
                "              \"++++\"\n" +
                "            ],\n" +
                "            \"keys\": [\n" +
                "              \"none\",\n" +
                "              \"+\",\n" +
                "              \"++\",\n" +
                "              \"+++\",\n" +
                "              \"++++\"\n" +
                "            ],\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result_hiv_viral_load\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"HIV Viral Load\",\n" +
                "            \"type\": \"spinner\",\n" +
                "            \"values\": [\n" +
                "              \"Detectable\",\n" +
                "              \"Undetectable\"\n" +
                "            ],\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result_hiv_viral_load_no\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"HIV Viral Load - Detectable\",\n" +
                "            \"type\": \"edit_text\",\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result_glucose\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"The result of the test conducted\",\n" +
                "            \"type\": \"edit_text\",\n" +
                "            \"edit_type\": \"number\",\n" +
                "            \"v_regex\": {\n" +
                "              \"value\": \"^[0-9]+(\\\\.)[0-9]+?$\",\n" +
                "              \"err\": \"Please enter a valid result\"\n" +
                "            },\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"diagnostic_test_result_specify\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"\",\n" +
                "            \"hint\": \"The result of the test conducted\",\n" +
                "            \"type\": \"edit_text\",\n" +
                "            \"relevance\": {\n" +
                "              \"rules-engine\": {\n" +
                "                \"ex-rules\": {\n" +
                "                  \"rules-dynamic\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"spacer\",\n" +
                "            \"openmrs_entity_parent\": \"\",\n" +
                "            \"openmrs_entity\": \"\",\n" +
                "            \"openmrs_entity_id\": \"spacer\",\n" +
                "            \"type\": \"spacer\",\n" +
                "            \"spacer_height\": \"40sp\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"relevance\": {\n" +
                "          \"rules-engine\": {\n" +
                "            \"ex-rules\": {\n" +
                "              \"rules-file\": \"opd/opd_laboratory_relevance_rules.yml\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }";

        Map<String, String> dict = new HashMap<>();
        dict.put("diagnostic_test_2ee0253c40794b3db867e26bc5d2a0eb", "hiv_test");
        dict.put("diagnostic_test_result_2ee0253c40794b3db867e26bc5d2a0eb", "Negative");
        dict.put("diagnostic_test_result_5a975423fd214144a84a44f5e565a4c0", "Inconclusive");
        dict.put("diagnostic_test_5a975423fd214144a84a44f5e565a4c0", "malaria_microscopy");

        JSONObject jsonObject = new JSONObject(jsonString);
        NativeFormProcessor processor = FormProcessorFactoryHelper.createInstance(jsonObject.toString());
        Map<String, Map<String, String>> values = processor.getRepeatingGroupValues(jsonObject, dict);

        Assert.assertEquals(values.get("2ee0253c40794b3db867e26bc5d2a0eb").get("diagnostic_test"), "hiv_test");
        Assert.assertEquals(values.get("2ee0253c40794b3db867e26bc5d2a0eb").get("diagnostic_test_result"), "Negative");

        Assert.assertEquals(values.get("5a975423fd214144a84a44f5e565a4c0").get("diagnostic_test"), "malaria_microscopy");
        Assert.assertEquals(values.get("5a975423fd214144a84a44f5e565a4c0").get("diagnostic_test_result"), "Inconclusive");

    }
}
