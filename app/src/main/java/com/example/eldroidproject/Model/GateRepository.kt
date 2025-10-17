package com.example.eldroidproject.Model

class GateRepository {

    private var gateModel = Gate(status = "Closed")

    fun getGateStatus(): Gate{
        return gateModel
    }

    fun updateGateStatus(newStatus: String) {
        gateModel = Gate(status = newStatus)
    }
}