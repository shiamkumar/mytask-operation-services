package com.ghx.api.operations.repository;



import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ghx.api.operations.model.MergeSupplierRequest;

/**
 * 
 * @author Sundari V
 * @since 03/11/2021
 */
public interface MergeSupplierRepository extends MongoRepository<MergeSupplierRequest, String> {

	@Query("{ $or: [{'deleteSupplier.oid' :  { $in: ?0 }} , {'retainSupplier.oid' : { $in: ?0 }}], status : { $in :[ 'CREATED', 'IN_PROGRESS', 'COMPLETED' ]} }")
	List<MergeSupplierRequest> getByVendorOids(List<String> supplierOid);

}
