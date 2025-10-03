<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
		<acme:list-column code="flight-crew-member.visa-requirements.form.label.passportName" path="passportName" width="20%" sortable="true"/>
		<acme:list-column code="flight-crew-member.visa-requirements.form.label.destinationName" path="destinationName" width="20%" sortable="true"/>
		<acme:list-column code="flight-crew-member.visa-requirements.form.label.assignmentId" path="assignmentId" width="20%" sortable="true"/>
		
		
</acme:list>
