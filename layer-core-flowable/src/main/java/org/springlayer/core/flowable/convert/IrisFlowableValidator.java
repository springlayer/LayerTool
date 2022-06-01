package org.springlayer.core.flowable.convert;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.ProcessLevelValidator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaoyl
 * @Date 2022-05-10
 * 规则验证类
 */
public class IrisFlowableValidator extends ProcessLevelValidator {

    private ValidationError validationErrorFactory(String aId, String aName, String msg, boolean warning) {
        ValidationError validationError = new ValidationError();
        validationError.setWarning(warning);
        validationError.setDefaultDescription(msg);
        validationError.setActivityName(aName);
        validationError.setActivityId(aId);
        return validationError;
    }

    @Override
    protected void executeValidation(BpmnModel bpmnModel,Process process, List<ValidationError> errors) {

        //是否设置任务接收人
        List<UserTask> userTaskList = process.findFlowElementsOfType(UserTask.class);
        if(CollectionUtils.isEmpty(userTaskList)){
            errors.add(validationErrorFactory(null,null,"找不到用户任务",true));
        }else{
            userTaskList.stream().filter(s -> StringUtils.isBlank(s.getName())).forEach(s -> {
                errors.add(validationErrorFactory(s.getId(),s.getName(),"找不到用户名",true));
            });
            userTaskList.stream().filter(s -> CollectionUtils.isEmpty(s.getCandidateUsers()) && CollectionUtils.isEmpty(s.getCandidateGroups())).forEach(s -> {
                errors.add(validationErrorFactory(s.getId(),s.getName(),s.getName()+"找不到任务收件人",false));
            });
        }

        //路线定义规则，如果该线路上一步是排他分支，则需要指定规则
        List<SequenceFlow> sequenceFlowList = process.findFlowElementsOfType(SequenceFlow.class);
        sequenceFlowList.stream().filter(s -> StringUtils.isBlank(s.getName())).forEach(s -> {
            errors.add(validationErrorFactory(s.getId(),s.getName(),"规则错误",true));
        });
        sequenceFlowList.stream().forEach(s->{
            FlowElement flow = process.getFlowElements().stream().filter(p->p.getId().equals(s.getSourceRef())).collect(Collectors.toList()).get(0);
            if(flow instanceof ExclusiveGateway && StringUtils.isBlank(s.getConditionExpression())){
                String sourceName = flow.getName();
                String targetName = process.getFlowElements().stream().filter(p->p.getId().equals(s.getTargetRef())).collect(Collectors.toList()).get(0).getName();
                errors.add(validationErrorFactory(s.getId(),s.getName(),sourceName+"至"+targetName+"找不到规则",false));
            }
        });
    }


}
