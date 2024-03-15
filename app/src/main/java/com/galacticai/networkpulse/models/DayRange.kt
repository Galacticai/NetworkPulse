package com.galacticai.networkpulse.models

import android.os.Parcel
import android.os.Parcelable

data class DayRange(
    val first: Long,
    val last: Long,
    val count: Int
) : Parcelable {
    constructor(parcel: Parcel) :
            this(parcel.readLong(), parcel.readLong(), parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(first)
        parcel.writeLong(last)
        parcel.writeInt(count)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<DayRange> {
        override fun createFromParcel(parcel: Parcel) = DayRange(parcel)
        override fun newArray(size: Int) = arrayOfNulls<DayRange?>(size)
    }
}
