package com.gemyoung;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by binli on 2016-02-17.
 */
public class ValidatorTools {
    private static Logger LOG = LoggerFactory.getLogger(ValidatorTools.class);
    private static Validator validator = null;

    private ValidatorTools() {
    }

    static {
        loadValidatorInstance();
    }

    /**
     * 校验对象
     *
     * @param validatorobj
     * @param errorCode
     * @param groups
     * @throws RuntimeException
     */
    public static void validatorBean(Object validatorobj, String errorCode, Class<?>... groups) throws RuntimeException {
        if (validator == null) {
            loadValidatorInstance();
        }
        LOG.warn("validator object start");
        if(validatorobj==null){
            LOG.error("validator obj is null");
            throw new RuntimeException("validator obj is null");
        }
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(validatorobj, groups);
        if (!constraintViolations.isEmpty()) {
            Iterator<ConstraintViolation<Object>> it = constraintViolations.iterator();
            StringBuilder sb = new StringBuilder();
            while (it.hasNext()) {
                sb.append(it.next().getMessage());
            }
            LOG.error("validator obj has errors,message is {}", sb.toString());
            throw new RuntimeException(sb.toString());
        }

    }

    /**
     * 初始化实例
     */
    private synchronized static void loadValidatorInstance() {
        if (validator == null) {
            LOG.info("validator begin to load instance");
            validator = Validation.buildDefaultValidatorFactory().getValidator();
            LOG.info("validator load instance over");

        }
    }
}
