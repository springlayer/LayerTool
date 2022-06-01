package org.springlayer.core.flowable.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.ui.common.security.SecurityUtils;
import org.flowable.ui.common.service.exception.BadRequestException;
import org.flowable.ui.common.util.XmlUtil;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.service.FlowableModelQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FlowableModelQueryServiceImpl extends FlowableModelQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowableModelQueryServiceImpl.class);

    @Override
    public ModelRepresentation importProcessModel(HttpServletRequest request, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && (fileName.endsWith(".bpmn") || fileName.endsWith(".bpmn20.xml"))) {
            try {
                XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
                InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
                BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
                if (CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
                    throw new BadRequestException("No process found in definition " + fileName);
                }

                if (bpmnModel.getLocationMap().size() == 0) {
                    BpmnAutoLayout bpmnLayout = new BpmnAutoLayout(bpmnModel);
                    bpmnLayout.execute();
                }

                ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);

                org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
                String name = process.getId();
                if (StringUtils.isNotEmpty(process.getName())) {
                    name = process.getName();
                }
                String description = process.getDocumentation();

                ModelRepresentation model = new ModelRepresentation();
                model.setKey(process.getId());
                model.setName(name);
                model.setDescription(description);
                model.setModelType(AbstractModel.MODEL_TYPE_BPMN);
                Model newModel = modelService.createModel(model, modelNode.toString(), SecurityUtils.getCurrentUserObject());
                return new ModelRepresentation(newModel);

            } catch (BadRequestException e) {
                throw e;

            } catch (Exception e) {
                LOGGER.error("Import failed for {}", fileName, e);
                throw new BadRequestException("Import failed for " + fileName + ", error message " + e.getMessage());
            }
        } else {
            throw new BadRequestException("Invalid file name, only .bpmn and .bpmn20.xml files are supported not " + fileName);
        }
    }
}
