package com.ghx.api.operations.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * 
 * @author Ajith
 *
 */
public class FileUtils {


    /** GHX logger */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(FileUtils.class);

    /**
     *
     * populating worksheet using input stream
     * @param mimeType
     * 
     * @param inputStream
     * @return Sheet
     * 
     */
    @SuppressWarnings("resource")
    public static Sheet prepareWorkSheet(String mimeType, InputStream inputStream) {
        Sheet worksheet;
        try {
            if (mimeType.equalsIgnoreCase(ConstantUtils.XLS_MIMETYPE)) {
                Workbook workbook = new HSSFWorkbook(inputStream);
                worksheet = workbook.getSheetAt(ConstantUtils.ZERO_INDEX);
            } else {
                Workbook workbook = new XSSFWorkbook(inputStream);
                worksheet = workbook.getSheetAt(ConstantUtils.ZERO_INDEX);
            }
        } catch (IOException exception) {
            LOGGER.error(CustomMessageSource.getMessage(ErrorConstants.INVALID_FILE), exception);
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.INVALID_FILE), exception);
        }
        return worksheet;
    }

    /**
     * check row is empty or not
     * @param row
     * @return boolean isEmpty
     */
    public static boolean isRowEmpty(Row row) {
        boolean isEmpty = true;
        if (Objects.nonNull(row)) {
            for (Cell cell : row) {
                if (cell.getCellType() != CellType.BLANK) {
                    isEmpty = false;
                    break;
                }
            }
        }
        return isEmpty;
    }

}
