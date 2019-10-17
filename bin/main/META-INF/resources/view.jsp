<%@ include file="/init.jsp" %>

<portlet:actionURL var="saveFMURL" />




<aui:form action="${saveFMURL}"  method="POST">
    ${formHtml}

    <aui:button type="submit" value="save"/>

</aui:form>


