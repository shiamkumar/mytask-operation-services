package com.ghx.api.operations.model;



import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Embeddable object for LookupVO.
 *
 * @author Selva & Parsu
 */
@Embeddable
@AttributeOverrides({
		@AttributeOverride(name = "code", column = @Column(name = "code")),
		@AttributeOverride(name = "category", column = @Column(name = "category")) })
public class LookupPK implements Serializable {

	private static final long serialVersionUID = -7460093900781470614L;

	private String code;

	private String category;

	/**
	 * Default constructor
	 */
	public LookupPK() {
	}

	/**
	 * @param code
	 * @param category
	 */
	public LookupPK(String code, String category) {
		this.code = code;
		this.category = category;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
     * {@inheritDoc}
     */
	public final boolean equals(final Object o) {

		LookupPK anotherPK = (LookupPK) o;

        if (null!=anotherPK && anotherPK.getCode().equalsIgnoreCase(this.code)
           && anotherPK.getCategory().equalsIgnoreCase(this.category)) {
			return true;
		}
		return false;
	}

	/**
     * {@inheritDoc}
     */
	public final int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
