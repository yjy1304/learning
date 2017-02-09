
package com.gemyoung;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by jinzita on 2016-09-14.
 */
public class FacadePreProcessAdvice {
    private static final Logger LOG = LoggerFactory.getLogger(FacadePreProcessAdvice.class);

    /**
     * do around
     * 此处只需返回Object类型的结果，但是实际返回类型应该是被代理方法的返回类型
     * @param pjp
     * @return
     */
    public Object doAround(ProceedingJoinPoint pjp) {
        long time = System.currentTimeMillis();
        Object[] args = pjp.getArgs();//获得请求的参数
        Class<?> clazz = pjp.getTarget().getClass();
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method targetMethod = methodSignature.getMethod();
        Object result = null;
        try {
            doBefore(clazz,targetMethod,args);

            result = pjp.proceed();

        } catch (CommonException ue) {//业务异常
            LOG.error("errorCode={}, errorMsg={}", ue.getErrorCode(), ue.getErrorMsg());
            result = genRetObj(targetMethod.getReturnType(), ue.getErrorCode(), ue.getErrorMsg());
        } catch (Throwable e) {//未知异常
            LOG.error("errorMsg={}",e);
            result = genRetObj(targetMethod.getReturnType(), "999999", e.getMessage());
        } finally {
            doAfter(clazz,targetMethod,result,time);
        }
        return result;
    }

    /**
     * do before
     * @param clz
     * @param method
     * @param args
     * @throws com.gemyoung.CommonException
     */
    public void doBefore(Class clz,Method method,Object[] args)throws CommonException{
        LOG.info("{}.{}, params={}", clz.getSimpleName(), method.getName(), args);
        //对注解参数进行验证
        validateArgs(args);
    }

    /**
     * do after
     * @param clazz
     * @param targetMethod
     * @param result
     * @param beginTime
     */
    public void doAfter(Class clazz,Method targetMethod,Object result,long beginTime){
        LOG.info("{}.{}, resp={}, time={}ms", clazz.getSimpleName(), targetMethod.getName(), result, System.currentTimeMillis() - beginTime);

    }

    /**
     * 生成指定类型的实例，若它是AbstractResponse类型的，设置其errCode和errMsg
     * @param returnType
     * @param errCode
     * @param errMsg
     * @return
     */
    private Object genRetObj(Class<?> returnType, String errCode, String errMsg) {
        if (returnType == null || returnType.getName().equals("void")) {
            return null;
        }
        Object result = null;
        try {
            result = returnType.newInstance();
        } catch (InstantiationException e) {
            LOG.error("无法实例化返回对象，返回值设置为null className={} e:{}", returnType.getSimpleName(), e);
        } catch (IllegalAccessException e) {
            LOG.error("无法访问返回对象默认构造方法，返回值设置为null className={} e:{}", returnType.getSimpleName(), e);
        }
        //可以根据类型设置errCode和errMsg
        return result;

    }

    /**
     * 循环验证方法入参
     * @param args
     * @throws com.gemyoung.CommonException
     * ption
     */
    private void validateArgs(Object[] args) throws CommonException {
        if (args == null || args.length == 0) {
            return;
        }
        for (Object arg : args) {
            ValidatorTools.validatorBean(arg, "99999");//可自定义参数校验失败的错误码
        }
    }

}
