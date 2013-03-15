require 'test_helper'

class UserTest < ActiveSupport::TestCase

	test "add User" do
		User.delete_all
		user = User.new(user: "TestUser", password: "abc", count: 0)
		assert user.save
		assert user.user == "TestUser"
		assert user.password == "abc"
		assert user.count == 0
		assert User.find_by_user("TestUser") == user
		User.delete_all
	end

	test "add Same User Fail" do
		user = User.new(user: "TestUser", password: "abc", count: 0)
		assert user.save
		assert user.user == "TestUser"
		assert user.password == "abc"
		assert user.count == 0
		assert User.find_by_user("TestUser") == user
		user2 = User.new(user: "TestUser", password: "efg", count: 0)
		assert !user2.save
		assert user2.errors[:user][0] == "has already been taken"
		User.delete_all
	end

	test "add Multiple Users Pass" do
		user1 = User.new(user: "User1", password: "pass1", count: 0)
		user2 = User.new(user: "User2", password: "pass2", count: 1)
		assert user1.save
		assert user2.save
		assert user1.user == "User1"
		assert user2.user == "User2"
		assert user1.password == "pass1"
		assert user2.password == "pass2"
		assert user1.count == 0
		assert user2.count == 1
		assert User.find_by_user("User1") == user1
		assert User.find_by_user("User2") == user2
	end	

	test "delete User" do
		user = User.new(user: "TestUser", password: "abc", count: 0)
		assert user.save
		assert user.user == "TestUser"
		assert user.password == "abc"
		assert user.count == 0
		assert User.find_by_user("TestUser") == user
		User.delete(user)
		assert User.find_by_user("TestUser") == nil
		User.delete_all
	end

	test "illegal User Name Empty" do
		user = User.new(user: "", password: "abc", count: 0)
		assert !user.save
		assert user.errors[:user][0] == "can't be blank"
		User.delete_all
	end

	test "illegal User Name Too Long" do
		illegalName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
		user = User.new(user: illegalName, password: "abc", count: 0)
		assert !user.save
		assert user.errors[:user][0] == "is too long (maximum is 128 characters)"
		User.delete_all
	end

	test "illegal Password Too Long" do
		illegalPassword = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
		user = User.new(user: "TestUser", password: illegalPassword, count: 0)
		assert !user.save
		assert user.errors[:password][0] == "is too long (maximum is 128 characters)"
		User.delete_all
	end

	test "emtpy Password Ok" do
		user = User.new(user: "Test", password: "", count: 0)
		assert user.save
		assert user.password == ""
		assert user.user == "Test"
		assert user.count == 0
		User.delete_all
	end

	test "increment Count Pass" do
		user = User.new(user: "Test", password: "", count: 0)
		assert user.save
		assert user.count == 0
		user.count = user.count + 1
		assert user.save
		assert user.count == 1
		user.count = user.count + 1
		assert user.save
		assert user.count == 2
	end

	test "user Does Not Exist" do
		user = User.find_by_user("Jim")
		assert user == nil
		user = User.find_by_user("Katie")
		assert user == nil
	end 

end