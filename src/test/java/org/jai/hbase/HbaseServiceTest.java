package org.jai.hbase;

import org.apache.flume.Event;
import org.jai.search.test.AbstractSearchJUnit4SpringContextTests;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

public class HbaseServiceTest extends AbstractSearchJUnit4SpringContextTests {

	@Autowired
	private HbaseService hbaseService;
	private int searchEventsCount = 200;

	@Test
	public void testHbaseServer() {
		hbaseService.testHbaseServer();
	}

	@Test
	public void testSearchClicksEventsData() {
		hbaseService.removeAll();
		for (Event event : generateSearchAnalyticsDataService
				.getSearchEvents(searchEventsCount)) {
			hbaseService.insertEventData(event.getBody());
		}
		assertEquals(searchEventsCount,
				hbaseService.getTotalSearchClicksCount());
	}

	@Test
	public void getSearchClicks() throws InterruptedException {
		hbaseService.removeAll();
		generateSearchAnalyticsDataService
				.generateAndPushSearchEvents(searchEventsCount);
		// wait 10 sec for the hbase data to get processed
		Thread.sleep(10000);
		hbaseService.getSearchClicks();
	}

	@Test
	public void testSearchClicksEventsDataForFlumeAgent()
			throws InterruptedException {
		hbaseService.removeAll();
		generateSearchAnalyticsDataService
				.generateAndPushSearchEvents(searchEventsCount);
		// wait 10 sec for the hbase data to get processed
		Thread.sleep(10000);
		assertEquals(searchEventsCount,
				hbaseService.getTotalSearchClicksCount());
	}

	@Test
	public void findTotalRecordsForValidCustomers() throws InterruptedException {
		hbaseService.removeAll();
		generateSearchAnalyticsDataService
				.generateAndPushSearchEvents(searchEventsCount);
		// wait 10 sec for the hbase data to get processed
		Thread.sleep(1000);
		assertTrue(hbaseService.getTotalSearchClicksCount() > 0);
	}

	@Test
	public void findTopTenSearchQueryStringForLastAnHour()
			throws InterruptedException {
		hbaseService.removeAll();
		generateSearchAnalyticsDataService.generateAndPushSearchEvents(1000);
		// wait 10 sec for the hbase data to get processed
		Thread.sleep(1000);
		assertTrue(hbaseService.findTopTenSearchQueryStringForLastAnHour()
				.size() > 0);
	}

	@Test
	public void findTopTenSearchFiltersForLastAnHour()
			throws InterruptedException {
		hbaseService.removeAll();
		generateSearchAnalyticsDataService.generateAndPushSearchEvents(1000);
		// wait 10 sec for the hbase data to get processed
		Thread.sleep(1000);
		assertTrue(hbaseService.findTopTenSearchFiltersForLastAnHour().size() > 0);
	}

	@Test
	public void findTopTenSearchFiltersForLastAnHourUsingRangeScan()
			throws InterruptedException {
		hbaseService.removeAll();
		generateSearchAnalyticsDataService.generateAndPushSearchEvents(2000);
		// wait 10 sec for the hbase data to get processed
		Thread.sleep(1000);
		assertTrue(hbaseService
				.findTopTenSearchFiltersForLastAnHourUsingRangeScan().size() > 0);
	}

	@Test
	public void numberOfTimesAFacetFilterClickedInLastAnHour()
			throws InterruptedException {
		hbaseService.removeAll();
		generateSearchAnalyticsDataService.generateAndPushSearchEvents(1000);
		// wait 10 sec for the hbase data to get processed
		Thread.sleep(1000);
		assertTrue(hbaseService.numberOfTimesAFacetFilterClickedInLastAnHour(
				"searchfacettype_brand_level_2", "Apple") > 0);
	}

	@Test
	public void getAllSearchQueryStringsByCustomerInLastOneMonth()
			throws InterruptedException {
		hbaseService.removeAll();
		generateSearchAnalyticsDataService.generateAndPushSearchEvents(1000);
		// wait 10 sec for the hbase data to get processed
		Thread.sleep(1000);

		// TODO: get exact customer id, passed value may not have been
		// generated, chances to fail.
		String customerId = hbaseService.getSearchClicksRowKeysWithValidQueryString().get(10).split("-")[0];
		assertTrue(hbaseService
				.getAllSearchQueryStringsByCustomerInLastOneMonth(
						Long.valueOf(customerId))
				.size() > 0);
	}

}
