package cn.starboot.socket.utils.page;

import cn.starboot.socket.utils.concurrent.collection.AbstractConcurrentWithCollection;
import cn.starboot.socket.utils.concurrent.collection.ConcurrentWithList;
import cn.starboot.socket.utils.concurrent.collection.ConcurrentWithSet;
import cn.starboot.socket.utils.convert.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class PageUtils {

	@SuppressWarnings("unchecked")
	public static <T> Page<T> fromList(ConcurrentWithList<T> list,
									   Integer pageIndex,
									   Integer pageSize,
									   Converter<T> converter) {
		if (list == null) {
			return null;
		}

		Page<Object> page = pre(list, pageIndex, pageSize);

		List<Object> pageData = page.getList();
		if (pageData == null) {
			return (Page<T>) page;
		}
		final int[] recordCount = {0};
		list.size(integer -> recordCount[0] = integer);
		int startIndex = Math.min((page.getPageNumber() - 1) * page.getPageSize(), recordCount[0]);
		int endIndex = Math.min(page.getPageNumber() * page.getPageSize(), recordCount[0]);

		for (int i = startIndex; i < endIndex; i++) {
			if (converter != null) {
				list.get(i, new Consumer<T>() {
					@Override
					public void accept(T t) {
						pageData.add(converter.convert(t));
					}
				});

			} else {
				list.get(i, new Consumer<T>() {
					@Override
					public void accept(T t) {
						pageData.add(t);
					}
				});
			}

		}
		page.setList(pageData);
		return (Page<T>) page;
	}


	public static <T> Page<T> fromSetWithLock(ConcurrentWithSet<T> setWithLock,
											  Integer pageIndex,
											  Integer pageSize) {
		return fromSetWithLock(setWithLock, pageIndex, pageSize, null);

	}

	public static <T> Page<T> fromSetWithLock(ConcurrentWithSet<T> concurrentWithSet,
											  Integer pageIndex,
											  Integer pageSize,
											  Converter<T> converter) {
		return fromSet(concurrentWithSet, pageIndex, pageSize, converter);
	}

	@SuppressWarnings("unchecked")
	public static <T> Page<T> fromSet(ConcurrentWithSet<T> concurrentWithSet,
									  Integer pageIndex,
									  Integer pageSize,
									  Converter<T> converter) {
		if (concurrentWithSet == null) {
			return null;
		}

		Page<Object> page = pre(concurrentWithSet, pageIndex, pageSize);

		List<Object> pageData = page.getList();
		if (pageData == null) {
			return (Page<T>) page;
		}
		final int[] recordCount = {0};
		concurrentWithSet.size(integer -> recordCount[0] = integer);
		int startIndex = Math.min((page.getPageNumber() - 1) * page.getPageSize(), recordCount[0]);
		int endIndex = Math.min(page.getPageNumber() * page.getPageSize(), recordCount[0]);


		concurrentWithSet.toArray(new Consumer<Object[]>() {
			@Override
			public void accept(Object[] objects) {
				int i = 0;
				for (Object t : objects) {
					if (i >= endIndex) {
						break;
					}
					if (i < startIndex) {
						i++;
						continue;
					}

					if (converter != null) {
						pageData.add(converter.convert(t));
					} else {
						pageData.add(t);
					}
					i++;
				}
			}
		});

		page.setList(pageData);
		return (Page<T>) page;
	}

	private static <T> Page<Object> pre(AbstractConcurrentWithCollection<? extends Collection<T>, T> allList,
									Integer pageIndex,
									Integer pageSize) {
		if (allList == null) {
			return new Page<>(null, pageIndex, pageSize, 0);
		}
		pageSize = processPageSize(pageSize);
		pageIndex = processpageNumber(pageIndex);
		final int[] recordCount = {0};
		allList.size(integer -> recordCount[0] = integer);

		if (pageSize > recordCount[0]) {
			pageSize = recordCount[0];
		}
		List<Object> pageData = new ArrayList<>(pageSize);
		return new Page<>(pageData, pageIndex, pageSize, recordCount[0]);
	}

	private static int processpageNumber(Integer pageIndex) {
		return pageIndex <= 0 ? 1 : pageIndex;
	}

	private static int processPageSize(Integer pageSize) {
		return pageSize <= 0 ? Integer.MAX_VALUE : pageSize;
	}

}
