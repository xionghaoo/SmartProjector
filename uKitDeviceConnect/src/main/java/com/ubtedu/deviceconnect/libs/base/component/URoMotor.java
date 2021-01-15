package com.ubtedu.deviceconnect.libs.base.component;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoRotateMotorCommand;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public class URoMotor extends URoComponent {

    public URoMotor(int componentId, String componentName, String version, URoProduct product) {
        super(componentId, componentName, version, product);
        componentType = URoComponentType.MOTOR;
    }

    public boolean rotate(int speed, int time, URoCompletionCallback<Void> callback) {
        return product.rotateMotors(new URoRotateMotorCommand[]{new URoRotateMotorCommand(componentId, speed, time)}, callback);
    }

//    public boolean rotateWithPWM(int pwmValue, URoCompletionCallback<Integer> callback) {
//        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_MOTOR_ROTATE_WITH_PWM);
//        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, componentId);
//        invocation.setParameter(URoInvocationParamKeys.Motor.PARAM_KEY_ROTATE_PWM, pwmValue);
//        invocation.setCompletionCallback(callback);
//        return invocation.sendToTarget(product);
//    }

    public boolean stop(URoCompletionCallback<Void> callback) {
        return product.stopMotors(new int[]{componentId}, callback);
    }

}
