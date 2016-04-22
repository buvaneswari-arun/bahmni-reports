package org.bahmni.reports.report.integrationtests;

import org.bahmni.reports.builder.ConceptBuilder;
import org.bahmni.reports.builder.EncounterBuilder;
import org.bahmni.reports.builder.OrderBuilder;
import org.bahmni.reports.builder.VisitBuilder;
import org.bahmni.reports.wrapper.Report;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.context.Context;

import static org.junit.Assert.assertEquals;

public class OrderFulfillmentReportTest extends BaseIntegrationTest {

    @Before
    public void setUp() {
        Concept concept = new ConceptBuilder().withClassId(3).withDataTypeId(2).withName("Plain Concept").withShortName("Plain Concept").build();
        Context.getConceptService().saveConcept(concept);

        Concept anotherConcept = new ConceptBuilder().withClassId(3).withDataTypeId(2).withName("Plain concept 2").withShortName("Plain concpet 2").build();
        Context.getConceptService().saveConcept(anotherConcept);

        Patient patient = Context.getPatientService().getPatient(2);

        Visit visit = new VisitBuilder().withPatient(patient).withVisitType(1).withStartDate("2015-11-02").build();
        Context.getVisitService().saveVisit(visit);

        EncounterType encounterType = Context.getEncounterService().getEncounterType(1);
        Encounter encounter = new EncounterBuilder().withPatient(patient).withDatetime("2015-11-02").withVisit(visit).withEncounterType(encounterType).build();
        Context.getEncounterService().saveEncounter(encounter);

        Provider provider = Context.getProviderService().getProvider(1);

        OrderType orderType = Context.getOrderService().getOrderType(17);
        Order order = new OrderBuilder().withOrderType(orderType).setDateActivated("2015-11-02").withConcept(concept).withPatient(patient).withEncounter(encounter).withOrderer(provider).build();
        Context.getOrderService().saveOrder(order, null);
    }

    @Test
    public void shouldRetrieveOrderFulfillmentReport() throws Exception {
        Report report = fetchReport("Order Fulfillment Report", "2000-10-13", "2016-10-21");
        assertEquals(8, report.columnsCount());
        assertEquals("Order Fulfillment Report", report.getReportName());
        assertEquals("Plain Order Plain Concept 02-Nov-2015 GAN1234 Horatio Hornblower M No", report.getRowAsString(1, " "));
        assertEquals("Plain Order", report.getColumnValueInRow(1, "Order Type"));
        assertEquals("GAN1234", report.getColumnValueInRow(1, "Patient ID"));
    }
}