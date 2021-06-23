package com.telematics.domain.model.statistics

import com.telematics.domain.R

enum class LeaderboardType(val index:Int) {
	Acceleration(1),
	Deceleration(2),
	Distraction(3),
	Speeding(4),
	Turn(5),
	Rate(6),
	Distance(7),
	Trips(8),
	Duration(9);
//	fun getIconRes():Int = when(this){
//		Acceleration -> R.drawable.ic_leaderboard_acceleration
//		Deceleration -> R.drawable.ic_leaderboard_deceleration
//		Distraction -> R.drawable.ic_leaderboard_phone
//		Speeding -> R.drawable.ic_leaderboard_speeding
//		Turn -> R.drawable.ic_leaderboard_cornering
//		Rate -> 0
//		Distance -> R.drawable.ic_leaderboard_mileage
//		Trips -> R.drawable.ic_leaderboard_trips
//		Duration -> R.drawable.ic_leaderboard_time
//	}
//
//	fun getStringRes():Int = when(this){
//		Acceleration -> R.string.leaderboard_acceleration
//		Deceleration -> R.string.leaderboard_deceleration
//		Distraction -> R.string.leaderboard_distraction
//		Speeding -> R.string.leaderboard_speeding
//		Turn -> R.string.leaderboard_turn
//		Rate -> R.string.leaderboard_rate
//		Distance -> R.string.leaderboard_mileage
//		Trips -> R.string.leaderboard_total_trips
//		Duration -> R.string.leaderboard_time_driven
//	}
	companion object{
		fun getFromIndex(index: Int):LeaderboardType{
			return values()[index-1]
		}
	}
}