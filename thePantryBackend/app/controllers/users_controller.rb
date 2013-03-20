class UsersController < ApplicationController

	def login
		user = User.find_by_user(params[:user])
		if user == nil
			render :json => { :errCode => -1 }
		elsif user.password != params[:password]
			render :json => { :errCode => -1 }
		else
			user.count += 1
			user.save
			render :json => { :errCode => 1, :count => user.count }
		end
	end

	def add
		user = User.new(user: params[:user], password: params[:password], count: 1)
		if user.save
			render :json => { :errCode => 1, :count => 1 }
		elsif user.errors[:user][0] == "has already been taken"
			render :json => { :errCode => -2 }
		elsif user.errors[:user][0] != nil
			render :json => { :errCode => -3 }
		else
			render :json => { :errCode => -4 }
		end
	end
end
