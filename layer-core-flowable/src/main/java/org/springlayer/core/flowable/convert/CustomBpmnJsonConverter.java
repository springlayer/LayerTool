package org.springlayer.core.flowable.convert;

import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.springframework.stereotype.Component;

/**
 * 目的是将BpmnJsonConverter中的 UserTaskJsonConverter 设置替换成 CustomizeUserTaskJsonConverter。
 * 为什么要继承 BpmnJsonConverter ？ 因为没办法，convertersToBpmnMap  convertersToJsonMap 是 protected 类型
 */
@Component
public class CustomBpmnJsonConverter extends BpmnJsonConverter {

    static {
        CustomizeUserTaskJsonConverter.fillTypes(convertersToBpmnMap, convertersToJsonMap);
    }
}
