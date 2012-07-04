package grails.plugin.formfields.mock

import grails.persistence.Entity

@Entity
class Stock {
	String name
	Double price
	/**
	 * Derived property of "name (price)"
	 */
	String tickerDisplay
	
	static mapping = {
		tickerDisplay formula: "concat(name, ' (', price, ')')"
	}
	
	@Override
	String toString() {
		name
	}
}
