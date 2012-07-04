package grails.plugin.formfields.taglib

import grails.plugin.formfields.mock.Person
import grails.plugin.formfields.mock.Stock
import grails.plugin.formfields.*
import grails.test.mixin.*
import spock.lang.*

@TestFor(FormFieldsTagLib)
@Mock([Person, Stock])
@Unroll
class AllTagSpec extends AbstractFormFieldsTagLibSpec {

	def mockFormFieldsTemplateService = Mock(FormFieldsTemplateService)

	def setupSpec() {
		configurePropertyAccessorSpringBean()
	}

	def setup() {
		def taglib = applicationContext.getBean(FormFieldsTagLib)

		mockFormFieldsTemplateService.findTemplate(_, 'field') >> [path: '/_fields/default/field']
		taglib.formFieldsTemplateService = mockFormFieldsTemplateService

		mockEmbeddedSitemeshLayout(taglib)
	}

	void "all tag renders fields for all properties"() {
		given:
		views["/_fields/default/_field.gsp"] = '${property} '

		when:
		def output = applyTemplate('<f:all bean="personInstance"/>', [personInstance: personInstance])

		then:
		output =~ /\bname\b/
		output =~ /\bpassword\b/
		output =~ /\bgender\b/
		output =~ /\bdateOfBirth\b/
		output =~ /\bminor\b/
	}

	@Issue('https://github.com/robfletcher/grails-fields/issues/21')
	void 'all tag skips #property property'() {
		given:
		views["/_fields/default/_field.gsp"] = '${property} '

		when:
		def output = applyTemplate('<f:all bean="personInstance"/>', [personInstance: personInstance])

		then:
		!output.contains(property)

		where:
		property << ['id', 'version', 'onLoad', 'lastUpdated', 'excludedProperty', 'displayFalseProperty']
	}

	@Issue('https://github.com/robfletcher/grails-fields/issues/12')
	void 'all tag skips properties listed with the except attribute'() {
		given:
		views["/_fields/default/_field.gsp"] = '${property} '

		when:
		def output = applyTemplate('<f:all bean="personInstance" except="password, minor"/>', [personInstance: personInstance])

		then:
		!output.contains('password')
		!output.contains('minor')
	}

	@Issue('https://github.com/robfletcher/grails-fields/issues/85')
	void 'all tag does not include derived properties'() {
		given:
		def stockInstance = new Stock(name: "COM", price: 10.5d)
		views["/_fields/default/_field.gsp"] = '${property}'
		
		when:
		def output = applyTemplate('<f:all bean="stockInstance" />', [stockInstance:stockInstance])

		then:
		output.contains('name')
		output.contains('price')
		!output.contains('tickerDisplay')
	}
}