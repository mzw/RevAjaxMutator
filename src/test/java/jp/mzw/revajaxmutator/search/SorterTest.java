package jp.mzw.revajaxmutator.search;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationListManager;
import jp.mzw.revajaxmutator.search.Sorter.SortType;

public class SorterTest {

	private static final String PATH_TO_MUTATAION_LIST = "src/test/resources/record-test/quizzy/mutants";
	private static MutationListManager manager;
	private static List<MutationFileInformation> mutants;

	@BeforeClass
	public static void setUpBeforeClass() throws JSONException, IOException {
		manager = new MutationListManager(PATH_TO_MUTATAION_LIST);
		manager.readExistingMutationListFile();
		mutants = manager.getMutationFileInformationList();
		Assert.assertNotNull(mutants);
		Assert.assertEquals(1567, mutants.size());
	}

	@Test
	public void testSortType() {
		SortType[] types = Sorter.SortType.values();
		Assert.assertEquals(19, types.length);
	}

	@Test
	public void testSorter() {
		Assert.assertNotNull(new Sorter(manager));
	}

	@Test
	public void testGetSortType() {
		SortType actual = Sorter.getSortType("SIMPLE_LOCATION");
		Assert.assertEquals(Sorter.SortType.SIMPLE_LOCATION, actual);
	}

	@Test
	public void testGetSortTypeInvalid() {
		SortType actual = Sorter.getSortType("invalid");
		Assert.assertEquals(SortType.REPAIR_SOURCE_DFS, actual);
	}

	@Test
	public void testCreateMap() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = Sorter.class.getDeclaredMethod("createMap", List.class);
		method.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<SortKey, List<MutationFileInformation>> map = (Map<SortKey, List<MutationFileInformation>>) method.invoke(new Sorter(manager), mutants);
		Assert.assertEquals(15, map.size());
	}

	@Test
	public void testSortByRsrepair()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		Sorter sorter = new Sorter(manager);

		Field field = Sorter.class.getDeclaredField("map");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<SortKey, List<MutationFileInformation>> map = (Map<SortKey, List<MutationFileInformation>>) field.get(sorter);

		Method method = Sorter.class.getDeclaredMethod("sortByRSRepair", Map.class, List.class);
		method.setAccessible(true);
		@SuppressWarnings("unchecked")
		List<MutationFileInformation> sorted1 = (List<MutationFileInformation>) method.invoke(sorter, map, mutants);
		@SuppressWarnings("unchecked")
		List<MutationFileInformation> sorted2 = (List<MutationFileInformation>) method.invoke(sorter, map, mutants);
		Assert.assertEquals(1567, sorted1.size());
		Assert.assertEquals(1567, sorted2.size());

		MutationFileInformation mutant1 = sorted1.get(0);
		MutationFileInformation mutant2 = sorted2.get(0);
		Assert.assertFalse(mutant1.getFileName().equals(mutant2.getFileName()));
	}

	@Test
	public void testCalculateDepthWithPercentage()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = Sorter.class.getDeclaredMethod("calculateDepthWithPercentage", int.class, int.class, int.class);
		method.setAccessible(true);
		int actual = (int) method.invoke(new Sorter(manager), 1567, 880, 5);
		Assert.assertEquals(2, actual);
	}

	@Test
	public void testCalculateDepthWithPercentage0To1()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = Sorter.class.getDeclaredMethod("calculateDepthWithPercentage", int.class, int.class, int.class);
		method.setAccessible(true);
		int actual = (int) method.invoke(new Sorter(manager), 1, 0, 0);
		Assert.assertEquals(1, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateDepthWithPercentageThrowingIllegalArgumentException()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = Sorter.class.getDeclaredMethod("calculateDepthWithPercentage", int.class, int.class, int.class);
		method.setAccessible(true);
		try {
			method.invoke(new Sorter(manager), 0, 0, 0);
		} catch (final InvocationTargetException e) {
			final Throwable throwable = e.getCause();
			if (throwable instanceof IllegalArgumentException) {
				throw (IllegalArgumentException) throwable;
			} else {
				Assert.fail();
			}
		}
		Assert.fail();
	}

	// TODO implement test cases for sorting functionality with each sort type
}
