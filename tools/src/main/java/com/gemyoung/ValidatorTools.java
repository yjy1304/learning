package com.gemyoung;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by gemyoung on 2017-02-08.
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
     * @param errorCode 可用于指定抛出异常的错误码
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
        //组是约束的子集，若指定了某个子集，将不会对ObjectGraph中的所有约束进行验证
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
        //判空要写到同步代码块中，这样就可以保证validator不为空时不再新建一个实例，如果是要反复执行的语句，在同步代码块外最好再加一层判空，这样性能更好
        if (validator == null) {
            LOG.info("validator begin to load instance");
            validator = Validation.buildDefaultValidatorFactory().getValidator();
            LOG.info("validator load instance over");

        }
    }
}
