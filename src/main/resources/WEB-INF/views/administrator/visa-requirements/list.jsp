<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
		<acme:list-column code="administrator.visa-requirements.form.label.passportName" path="passportName" width="20%" sortable="true"/>
		<acme:list-column code="adminisitrator.visa-requirements.form.label.destinationName" path="destinationName" width="20%" sortable="true"/>
		<acme:list-column code="adminisitrator.visa-requirements.form.label.assignment" path="assignment" width="20%" sortable="true"/>
</acme:list>
<acme:submit code="administrator.form.button.update" action="/administrator/visa-requirements/update"/>
