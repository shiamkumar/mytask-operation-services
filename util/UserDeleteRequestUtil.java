package com.ghx.api.operations.util;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.messagesource.CustomMessageSource;

/**
 * 
 * @author Ajith
 *
 */
public class UserDeleteRequestUtil {
	
    /**
     * validate WorkSheet
     * @param worksheet
     * @param usersDeleteLimit
     */
    public static void validateWorkSheet(Sheet worksheet, int usersDeleteLimit) {
        if (worksheet.getLastRowNum() > usersDeleteLimit) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.USERS_DELETE_LIMIT_EXCEED, usersDeleteLimit));
        }
    }
    
    /**
     * validate Empty Users
     * @param usersToBeDeletedSet
     */
    public static void validateEmptyUsers(Set<String> usersToBeDeletedSet) {
        if (CollectionUtils.isEmpty(usersToBeDeletedSet)) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.EMPTY_FILE));
        }
    }

    /**
     * validate MimeType
     * @param mimeType
     */
    public static void validateMimeType(String mimeType) {
        if (!StringUtils.equalsAnyIgnoreCase(mimeType, ConstantUtils.XLS_MIMETYPE, ConstantUtils.XLSX_MIMETYPE)) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.INVALID_FILE_FORMAT));
        }
    }

}
