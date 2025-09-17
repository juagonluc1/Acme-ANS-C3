<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.leg.form.label.flightNumber" path="flightNumber"/>
	<acme:input-moment code="any.leg.form.label.scheduledDeparture" path="scheduledDeparture"/>
	<acme:input-moment code="any.leg.form.label.scheduledArrival" path="scheduledArrival"/>
	<acme:input-select code="any.leg.form.label.status" path="status" choices="${statuses}"/>
	<acme:input-select code="any.leg.form.label.departureAirport" path="departureAirport" choices="${departureAirports}"/>
	<acme:input-select code="any.leg.form.label.arrivalAirport" path="arrivalAirport" choices="${arrivalAirports}"/>
	<acme:input-select code="any.leg.form.label.aircraft" path="aircraft" choices="${aircraftChoices}"/>
</acme:form>
