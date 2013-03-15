class TestapiController < ApplicationController

	def resetFixture
		User.delete_all
		render :json => { :errCode => 1}
	end

	def unitTests
		value = %x(ruby -Itest test/unit/user_test.rb) 
		tests = /(\d+) tests,/.match(value)
		failures = /(\d+) failures,/.match(value)
		render :json => {:totalTests => Integer(tests[1]), :nrFailed => Integer(failures[1]), :output => value }
	end

end
