package org.springlayer.core.flowable.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springlayer.core.flowable.utils.ExtensionAttributeUtils;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.ActivityProcessor;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.UserTaskJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 新建自定义userTaskjson解析器CustomizeUserTaskJsonConverter
 */
public class CustomizeUserTaskJsonConverter extends UserTaskJsonConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomizeUserTaskJsonConverter.class);

    private static final String NODE_TYPE_KEY = "nodetype";
    private static final String REVOKE_FLAG_KEY = "revokeflag";
    private static final String END_FLAG_KEY = "endflag";
    private static final String DUEDATE_FLAG_KEY = "duedatedefinition";

    /**
     * 覆盖原 UserTaskJsonConverter 中的 fillTypes 方法
     *
     * @param convertersToBpmnMap map
     * @param convertersToJsonMap map
     * @see org.flowable.editor.language.json.converter.BpmnJsonConverter  中的静态代码块
     */
    public static void fillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap,
                                 Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {

        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    /**
     * 覆盖原 UserTaskJsonConverter 中的 fillBpmnTypes 方法
     *
     * @param convertersToJsonMap map
     * @see org.flowable.editor.language.json.converter.BpmnJsonConverter  中的静态代码块
     */
    public static void fillBpmnTypes(
            Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(UserTask.class, CustomizeUserTaskJsonConverter.class);
    }

    /**
     * 覆盖原 UserTaskJsonConverter 中的 fillJsonTypes 方法
     *
     * @param convertersToBpmnMap map
     * @see org.flowable.editor.language.json.converter.BpmnJsonConverter  中的静态代码块
     */
    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_TASK_USER, CustomizeUserTaskJsonConverter.class);
    }

    /**
     * 用于 从外部xml中导入流程
     *
     * @param baseElement     ignore
     * @param processor       ignore
     * @param model           ignore
     * @param container       ignore
     * @param shapesArrayNode ignore
     * @param subProcessX     ignore
     * @param subProcessY     ignore
     */
    @Override
    public void convertToJson(BaseElement baseElement, ActivityProcessor processor, BpmnModel model, FlowElementsContainer container, ArrayNode shapesArrayNode, double subProcessX, double subProcessY) {
        super.convertToJson(baseElement, processor, model, container, shapesArrayNode, subProcessX, subProcessY);
        if (baseElement instanceof UserTask) {
            LOGGER.debug("进入自定义属性解析 CustomizeUserTaskJsonConverter-{} ...", "convertToJson");
            LOGGER.debug("userTaskId = {} 扩展属性 = {}", baseElement.getId(), baseElement.getAttributes());
            Map<String, List<ExtensionAttribute>> stringListMap = baseElement.getAttributes();

            String nodetype = stringListMap.get(NODE_TYPE_KEY).get(0).getValue();
            String revokeflag = stringListMap.get(REVOKE_FLAG_KEY).get(0).getValue();
            String endflag = stringListMap.get(END_FLAG_KEY).get(0).getValue();
            String duedatedefinition = stringListMap.get(DUEDATE_FLAG_KEY).get(0).getValue();

            String resourceId = "resourceId";
            shapesArrayNode.forEach(node -> {
                if (baseElement.getId().equals(node.get(resourceId).textValue())) {
                    ObjectNode properties = (ObjectNode) node.get("properties");
                    properties.set(DUEDATE_FLAG_KEY, new TextNode(duedatedefinition));
                    properties.set(NODE_TYPE_KEY, new TextNode(nodetype));
                    properties.set(REVOKE_FLAG_KEY, BooleanNode.valueOf(Boolean.parseBoolean(revokeflag)));
                    properties.set(END_FLAG_KEY, BooleanNode.valueOf(Boolean.parseBoolean(endflag)));
                }
            });
        }
    }


    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode,
                                               Map<String, JsonNode> shapeMap) {
        FlowElement flowElement = super.convertJsonToElement(elementNode, modelNode, shapeMap);

        if (flowElement instanceof UserTask) {
            LOGGER.debug("进入自定义属性解析 CustomizeUserTaskJsonConverter-{}, 当前任务名称={} ...",
                    "convertJsonToElement", getPropertyValueAsString("name", elementNode));

            Map<String, List<ExtensionAttribute>> attributes = flowElement.getAttributes();
            String nodetype = getPropertyValueAsString(NODE_TYPE_KEY, elementNode);
            if (StringUtils.isEmpty(nodetype)) {
                LOGGER.debug("nodetype 属性为空，设置为默认值");
                nodetype = "commit";
            }
            attributes.put(NODE_TYPE_KEY, Collections.singletonList(
                    ExtensionAttributeUtils.generate(NODE_TYPE_KEY, nodetype)));

            String revokeflag = getPropertyValueAsString(REVOKE_FLAG_KEY, elementNode);
            if (StringUtils.isEmpty(revokeflag)) {
                LOGGER.debug("revokeflag 属性为空，设置为默认值");
                revokeflag = "false";
            }
            attributes.put(REVOKE_FLAG_KEY, Collections.singletonList(
                    ExtensionAttributeUtils.generate(REVOKE_FLAG_KEY, revokeflag)));

            String endflag = getPropertyValueAsString(END_FLAG_KEY, elementNode);
            if (StringUtils.isEmpty(endflag)) {
                LOGGER.debug("endflag 属性为空，设置为默认值");
                endflag = "false";
            }
            attributes.put(END_FLAG_KEY, Collections.singletonList(
                    ExtensionAttributeUtils.generate(END_FLAG_KEY, endflag)));

            String duedatedefinition = getProperty(DUEDATE_FLAG_KEY, elementNode).toString();
            if (StringUtils.isEmpty(duedatedefinition)) {
                LOGGER.debug("duedatedefinition 属性为空，设置为默认值");
                duedatedefinition = "";
            }
            attributes.put(DUEDATE_FLAG_KEY, Collections.singletonList(
                    ExtensionAttributeUtils.generate(DUEDATE_FLAG_KEY, duedatedefinition)));


            LOGGER.debug("自定义属性解析CustomizeUserTaskJsonConverter 完成");
            LOGGER.debug("当前 attributes 为 {}", attributes);
        }

        return flowElement;
    }


}
