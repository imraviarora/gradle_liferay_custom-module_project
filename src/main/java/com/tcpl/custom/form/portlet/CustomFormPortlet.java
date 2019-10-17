package com.tcpl.custom.form.portlet;

import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingException;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.tcpl.custom.form.constants.CustomFormPortletKeys;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author 10688
 */
@Component(immediate = true, property = { 
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css", "com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=CustomForm", "javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp", "javax.portlet.name=" + CustomFormPortletKeys.CUSTOMFORM,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user" 
		}, service = Portlet.class)
public class CustomFormPortlet extends MVCPortlet {

	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		System.out.println(" inside method doView ,,,,,,,,,,,,,,,,,,,:: ");
		DDMFormInstance ddmFormInstance = DDMFormInstanceLocalServiceUtil.fetchDDMFormInstance(36523L);
		DDMStructureVersion ddmStructureVersion = null;

		System.out.println(" formInstanceRecordId :: " + ddmFormInstance);
		String html = StringPool.BLANK;
		try {

			ddmStructureVersion = ddmFormInstance.getStructure().getLatestStructureVersion();
			System.out.println(" inside method doView :: " + ddmStructureVersion);
		} catch (PortalException e) {
			e.printStackTrace();
		}

		com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = ddmStructureVersion.getDDMForm();
		com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout = null;

		try {
			ddmFormLayout = ddmStructureVersion.getDDMFormLayout();
		} catch (PortalException e) {
			e.printStackTrace();
		}

		DDMFormRenderingContext ddmFormRenderingContext = new DDMFormRenderingContext();

		ddmFormRenderingContext.setContainerId("ddmForm".concat(StringUtil.randomString()));

		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		ddmFormRenderingContext.setHttpServletRequest(httpServletRequest);
		ddmFormRenderingContext.setHttpServletResponse(PortalUtil.getHttpServletResponse(renderResponse));
		ddmFormRenderingContext.setLocale(PortalUtil.getLocale(renderRequest));
		ddmFormRenderingContext.setPortletNamespace(renderResponse.getNamespace());
		ddmFormRenderingContext.setViewMode(false);
		ddmFormRenderingContext.setShowSubmitButton(true);
		ddmFormRenderingContext.setShowRequiredFieldsWarning(true);

		try {
			html = _ddmFormRenderer.render(ddmForm, ddmFormLayout, ddmFormRenderingContext);
		} catch (DDMFormRenderingException e) {
			e.printStackTrace();
		}
		renderRequest.setAttribute("formHtml", html);

		super.doView(renderRequest, renderResponse);
	}

	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {

		DDMFormInstance ddmFormInstance = DDMFormInstanceLocalServiceUtil.fetchDDMFormInstance(35513L);
		DDMStructureVersion ddmStructureVersion = null;
		try {
			ddmStructureVersion = ddmFormInstance.getStructure().getLatestStructureVersion();
		} catch (PortalException e) {
			e.printStackTrace();
		}
		com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = ddmStructureVersion.getDDMForm();
		DDMFormValues ddmFormValues = _ddmFormValuesFactory.create(actionRequest, ddmForm);
		 ServiceContext serviceContext = null;
		try {
            serviceContext = ServiceContextFactory.getInstance(
                DDMFormInstanceRecord.class.getName(), actionRequest);
        } catch (PortalException e) {
            e.printStackTrace();
        }
		DDMFormInstanceRecord ddmFormInstanceRecord = null;
		 try {
			 ddmFormInstanceRecord = DDMFormInstanceRecordLocalServiceUtil.addFormInstanceRecord(serviceContext.getUserId(), serviceContext.getScopeGroupId(),
                     ddmFormInstance.getFormInstanceId(), ddmFormValues,
                     serviceContext);
			 
	     } catch (PortalException e) {
             e.printStackTrace();
         }	 
	}
	
	
	@Reference(unbind = "-")
	protected void setDDMFormValuesFactory(
		DDMFormValuesFactory ddmFormValuesFactory) {
		_ddmFormValuesFactory = ddmFormValuesFactory;
	}

	@Reference(unbind = "-")
	protected void setDDMFormRenderer(DDMFormRenderer ddmFormRenderer) {
		_ddmFormRenderer = ddmFormRenderer;
	}

	DDMFormRenderer _ddmFormRenderer;
	DDMFormValuesFactory _ddmFormValuesFactory;

	DDMStructureVersion ddmStructureVersion;

}