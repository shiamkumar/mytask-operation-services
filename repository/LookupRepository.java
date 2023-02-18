package com.ghx.api.operations.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.model.LookupVO;
/**
 * Interface LookupRepository
 * @author Ananth Kandasamy
 *
 */
@Repository
public interface LookupRepository extends JpaRepository<LookupVO, String>, LookupRepositoryCustom {

    LookupVO findByCategoryAndCode(String category, String code);

    LookupVO findByCategoryAndCodeAndParentOid(String category, String code, String parentOid);

    List<LookupVO> findByCategory(String category, Pageable pageable);
    
    List<LookupVO> findByCategoryAndDescription(String category, String description);
    
    /**
     * get lookup details by code
     * @param feins
     * @return
     */
    @Query("SELECT l FROM LookupVO l WHERE l.code in (:feins) ")
    List<LookupVO> findByCode(List<String> feins);

    /**
     * Get pricing code by category
     * @param category
     * @return
     */
    @Query("SELECT l.code FROM LookupVO l WHERE l.category = :category ")
    List<String> findCodeByCategory(String category);

    /**
     * @param code
     * @return
     */
    @Query("select l.category||' - '|| l.description from LookupVO l where l.code = :code ")
    String findCategoryAndDescriptionByCode(String code);

}
