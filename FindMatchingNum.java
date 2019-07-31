import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FindMatchingNum {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		long num = sc.nextLong();

		Map<Long, Long> map1 = new HashMap();
		Map<Long, Long> map2 = new HashMap();
		List<Long> list1 = new ArrayList<>();
		List<Long> list2 = new ArrayList<>();
		for (long i = 0; i < num; i++) {
			long m = sc.nextLong();
			list1.add(m);
			if (map1.containsKey(m)) {
				map1.put(m, map1.get(m) + 1);
			} else {
				map1.put(m, 1L);
			}

			long n = sc.nextLong();
			list2.add(n);
			if (map2.containsKey(n)) {
				map2.put(n, map2.get(n) + 1);
			} else {
				map2.put(n, 1L);
			}
		}

		long max1 = 1;
		long key1 = 1;
		for (Map.Entry entry : map1.entrySet()) {
			if ((long) entry.getValue() > max1) {
				max1 = (long) entry.getValue();
				key1 = (long) entry.getKey();
			}
		}

		System.out.println(key1);
		long max2 = 1;
		long key2 = 1;
		for (Map.Entry entry : map2.entrySet()) {
			if ((long) entry.getValue() > max2) {
				max2 = (long) entry.getValue();
				key2 = (long) entry.getKey();
			}
		}

		System.out.println(key2);
		int size;
		for (size = 0; size < num; size++) {
			if (list1.get(size) == key1 || list2.get(size) == key2) {
				continue;
			} else {
				break;
			}
		}

		if (size == num - 1) {
			System.out.println(key1 + " " + key2);
		} else {
			System.out.println("No");
		}
	}
}
