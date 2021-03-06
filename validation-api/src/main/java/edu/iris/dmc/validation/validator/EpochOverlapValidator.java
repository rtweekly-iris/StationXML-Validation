package edu.iris.dmc.validation.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import edu.iris.dmc.fdsn.station.model.NodeType;
import edu.iris.dmc.validation.rule.NoOverlap;

public class EpochOverlapValidator implements ConstraintValidator<NoOverlap, Collection<?>> {
	private NoOverlap constraintAnnotation;

	@Override
	public void initialize(NoOverlap constraintAnnotation) {
		this.constraintAnnotation = constraintAnnotation;

	}

	@Override
	public boolean isValid(Collection<?> collection, ConstraintValidatorContext context) {
		if (collection == null || collection.isEmpty()) {
			// null collection cannot be validated
			return true;
		}
		int i = 0;
		Map<String, List<Tuple>> map = new HashMap<String, List<Tuple>>();
		for (Object object : collection) {
			if (!(object instanceof NodeType)) {
				// throw
			}
			NodeType node = (NodeType) object;
			Date start = node.getStartDate();
			Date end = null;

			if (node.getEndDate() != null) {
				end = node.getEndDate();
			}
			List<Tuple> tuples = map.get(node.getCode());
			if (tuples == null) {
				tuples = new ArrayList<Tuple>();
				map.put(node.getCode(), tuples);
			}
			tuples.add(new Tuple(node.getCode(), node.getCode(), start, end, i));
			i++;
			node.getEndDate();
			node.getStartDate();
		}

		if (!map.isEmpty()) {
			Collection<List<Tuple>> coll = map.values();
			for (List<Tuple> tuples : coll) {
				List<Tuple[]> invalidRanges = checkRanges(tuples);
				if (invalidRanges != null && !invalidRanges
						.isEmpty()) {/*
										 * if (violation == null) { violation =
										 * new Violation(this); }
										 * 
										 * for (Tuple[] array : invalidRanges) {
										 * violation.addMessage(array[0].code +
										 * " " // + array[0].location +
										 * " epochs overlaps [base epoch endTime: "
										 * + array[0].end +
										 * " | other epoch startTime: " +
										 * array[1].start + "]"); }
										 */
					return false;
				}
			}
		}
		return true;
	}

	private List<Tuple[]> checkRanges(List<Tuple> tuples) {
		List<Tuple[]> overlappingDatePairs = new ArrayList<Tuple[]>();
		Collections.sort(tuples);
		for (int i = 1; i < tuples.size(); i++) {
			Tuple tuple1 = tuples.get(i - 1);
			Tuple tuple2 = tuples.get(i);
			// }Tuple tuple : tuples) {
			if (tuple1.end.getTime() > tuple2.start.getTime()) {
				overlappingDatePairs.add(new Tuple[] { tuple1, tuple2 });
			}
		}
		return overlappingDatePairs;
	}

	private static class Tuple implements Comparable<Tuple> {
		public final Date start;
		private Date end;
		public final int id;
		public String code;
		public String location;

		public Tuple(String code, String location, Date start, Date end, int id) {
			this.code = code;
			this.location = location;
			this.start = start;
			this.end = end;
			this.id = id;
		}

		public int compareTo(Tuple other) {
			return start.compareTo(other.start);
			/*
			 * if (dateCompare != 0) { return dateCompare; } else { if (!isStart
			 * && other.isStart) { return -1; } else if (isStart &&
			 * !other.isStart) { return 1; } else { return 0; } }
			 */
		}
	}
}
