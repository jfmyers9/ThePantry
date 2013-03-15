class User < ActiveRecord::Base
  attr_accessible :count, :password, :user

  validates :user, presence: true, length: { maximum: 128 }, uniqueness: true
  validates :password, length: { maximum: 128 }
end
