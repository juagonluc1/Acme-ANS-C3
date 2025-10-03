<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
		<acme:input-textbox code="administrator.visa-requirements.form.label.passportName" path="passportName" readonly="true"/>
		<acme:input-textbox code="administrator.visa-requirements.form.label.destinationName" path="destinationName" readonly="true"/>
		<acme:input-textbox code="administrator.visa-requirements.form.label.nameCategory" path="nameCategory" readonly="true"/>
		<acme:input-textbox code="administrator.visa-requirements.form.label.duration" path="duration" readonly="true"/>
		<acme:input-moment code="administrator.visa-requirements.form.label.lastUpdate" path="lastUpdate" readonly="true"/>
</acme:form>

