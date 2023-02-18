package com.ghx.api.operations.problem;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * Class ResourceNotFoundProblem: this class is holds logic for excetption handle  for 404  not found exception
 * @author ananth.k
 *
 */
public class ResourceNotFoundProblem extends AbstractThrowableProblem {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor for ResourceNotFoundProblem
	 * 
	 * @param type
	 * @param errorMessage
	 * @param httpStatus
	 */
	public ResourceNotFoundProblem(URI type, String errorMessage, Status httpStatus) {
		super(type, Status.NOT_FOUND.name(), Objects.nonNull(httpStatus) ? httpStatus : Status.NOT_FOUND, null, null,
				null, getAlertParameters(errorMessage));
	}

	private static Map<String, Object> getAlertParameters(String errorKey) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("detail", errorKey);
		return parameters;
	}

}
