package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppLanguage
import com.example.model.AppRole
import com.example.model.BoardingStatus
import com.example.model.BusRoute
import com.example.model.PaymentMethod
import com.example.model.PaymentReceipt
import com.example.model.PickupPass
import com.example.model.SafetyNotification
import com.example.model.Student
import com.example.model.TripMode
import org.json.JSONArray
import org.json.JSONObject

data class PersistedAppState(
    val students: List<Student>,
    val routes: List<BusRoute>,
    val receipts: List<PaymentReceipt>,
    val notifications: List<SafetyNotification>,
    val pickupPasses: List<PickupPass>,
    val activeRole: AppRole,
    val language: AppLanguage,
    val selectedParentId: String,
    val selectedRouteId: String
)

class SafeRideRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("saferide_edupay_storage_v1", Context.MODE_PRIVATE)

    fun loadState(): PersistedAppState {
        val raw = prefs.getString(KEY_STATE, null)
        if (raw.isNullOrBlank()) {
            val fresh = defaultState()
            saveState(fresh)
            return fresh
        }
        return try {
            decodeState(JSONObject(raw))
        } catch (_: Exception) {
            val fresh = defaultState()
            saveState(fresh)
            fresh
        }
    }

    fun saveState(state: PersistedAppState) {
        prefs.edit().putString(KEY_STATE, encodeState(state).toString()).apply()
    }

    fun resetDemoData(): PersistedAppState {
        val fresh = defaultState()
        saveState(fresh)
        return fresh
    }

    private fun defaultState(): PersistedAppState = PersistedAppState(
        students = DemoSeedData.initialStudents(),
        routes = DemoSeedData.initialRoutes(),
        receipts = DemoSeedData.initialReceipts(),
        notifications = DemoSeedData.initialNotifications(),
        pickupPasses = DemoSeedData.initialPickupPasses(),
        activeRole = AppRole.ADMIN,
        language = AppLanguage.EN,
        selectedParentId = DemoSeedData.parentProfiles.first().id,
        selectedRouteId = DemoSeedData.initialRoutes().first().id
    )

    private fun encodeState(state: PersistedAppState): JSONObject {
        val root = JSONObject()
        root.put("activeRole", state.activeRole.name)
        root.put("language", state.language.name)
        root.put("selectedParentId", state.selectedParentId)
        root.put("selectedRouteId", state.selectedRouteId)

        val studentsArr = JSONArray()
        state.students.forEach { s ->
            studentsArr.put(JSONObject().apply {
                put("id", s.id)
                put("fullName", s.fullName)
                put("amharicName", s.amharicName)
                put("grade", s.grade)
                put("parentId", s.parentId)
                put("parentName", s.parentName)
                put("parentPhone", s.parentPhone)
                put("telegramHandle", s.telegramHandle)
                put("busRouteId", s.busRouteId)
                put("pickupStopName", s.pickupStopName)
                put("tuitionFeeEtb", s.tuitionFeeEtb)
                put("busFeeEtb", s.busFeeEtb)
                put("totalPaidEtb", s.totalPaidEtb)
                put("boardingStatus", s.boardingStatus.name)
                put("lastBoardingTimestamp", s.lastBoardingTimestamp)
                put("lastBoardingNote", s.lastBoardingNote)
            })
        }
        root.put("students", studentsArr)

        val routesStateArr = JSONArray()
        state.routes.forEach { r ->
            routesStateArr.put(JSONObject().apply {
                put("id", r.id)
                put("isLiveBroadcasting", r.isLiveBroadcasting)
                put("tripMode", r.tripMode.name)
                put("currentStopIndex", r.currentStopIndex)
                put("progressFraction", r.progressFraction.toDouble())
                put("etaMinutes", r.etaMinutes)
            })
        }
        root.put("routesState", routesStateArr)

        val receiptsArr = JSONArray()
        state.receipts.forEach { rc ->
            receiptsArr.put(JSONObject().apply {
                put("receiptNumber", rc.receiptNumber)
                put("studentId", rc.studentId)
                put("studentName", rc.studentName)
                put("grade", rc.grade)
                put("parentName", rc.parentName)
                put("tuitionPortionEtb", rc.tuitionPortionEtb)
                put("busPortionEtb", rc.busPortionEtb)
                put("amountPaidEtb", rc.amountPaidEtb)
                put("method", rc.method.name)
                put("referenceNumber", rc.referenceNumber)
                put("timestamp", rc.timestamp)
                put("verifiedBy", rc.verifiedBy)
            })
        }
        root.put("receipts", receiptsArr)

        val notifArr = JSONArray()
        state.notifications.forEach { n ->
            notifArr.put(JSONObject().apply {
                put("id", n.id)
                put("studentId", n.studentId)
                put("studentName", n.studentName)
                put("parentId", n.parentId)
                put("busCode", n.busCode)
                put("titleEn", n.titleEn)
                put("titleAm", n.titleAm)
                put("detailEn", n.detailEn)
                put("detailAm", n.detailAm)
                put("timestamp", n.timestamp)
                put("type", n.type.name)
            })
        }
        root.put("notifications", notifArr)

        val passesArr = JSONArray()
        state.pickupPasses.forEach { p ->
            passesArr.put(JSONObject().apply {
                put("id", p.id)
                put("code", p.code)
                put("studentId", p.studentId)
                put("studentName", p.studentName)
                put("parentId", p.parentId)
                put("delegateName", p.delegateName)
                put("relationship", p.relationship)
                put("delegatePhone", p.delegatePhone)
                put("validDate", p.validDate)
                put("isVerified", p.isVerified)
                put("verifiedAt", p.verifiedAt ?: "")
            })
        }
        root.put("pickupPasses", passesArr)

        return root
    }

    private fun decodeState(root: JSONObject): PersistedAppState {
        val activeRole = AppRole.valueOf(root.optString("activeRole", AppRole.ADMIN.name))
        val language = AppLanguage.valueOf(root.optString("language", AppLanguage.EN.name))
        val selectedParentId = root.optString("selectedParentId", DemoSeedData.parentProfiles.first().id)
        val selectedRouteId = root.optString("selectedRouteId", "ROUTE-A")

        val studentsArr = root.getJSONArray("students")
        val students = (0 until studentsArr.length()).map { idx ->
            val o = studentsArr.getJSONObject(idx)
            Student(
                id = o.getString("id"),
                fullName = o.getString("fullName"),
                amharicName = o.getString("amharicName"),
                grade = o.getString("grade"),
                parentId = o.getString("parentId"),
                parentName = o.getString("parentName"),
                parentPhone = o.getString("parentPhone"),
                telegramHandle = o.getString("telegramHandle"),
                busRouteId = o.getString("busRouteId"),
                pickupStopName = o.getString("pickupStopName"),
                tuitionFeeEtb = o.getInt("tuitionFeeEtb"),
                busFeeEtb = o.getInt("busFeeEtb"),
                totalPaidEtb = o.getInt("totalPaidEtb"),
                boardingStatus = BoardingStatus.valueOf(o.getString("boardingStatus")),
                lastBoardingTimestamp = o.getString("lastBoardingTimestamp"),
                lastBoardingNote = o.getString("lastBoardingNote")
            )
        }

        val routeSeeds = DemoSeedData.initialRoutes()
        val routeStateMap = mutableMapOf<String, JSONObject>()
        val routesArr = root.optJSONArray("routesState")
        if (routesArr != null) {
            for (i in 0 until routesArr.length()) {
                val o = routesArr.getJSONObject(i)
                routeStateMap[o.getString("id")] = o
            }
        }
        val routes = routeSeeds.map { seed ->
            val saved = routeStateMap[seed.id]
            if (saved != null) {
                seed.copy(
                    isLiveBroadcasting = saved.optBoolean("isLiveBroadcasting", seed.isLiveBroadcasting),
                    tripMode = TripMode.valueOf(saved.optString("tripMode", seed.tripMode.name)),
                    currentStopIndex = saved.optInt("currentStopIndex", seed.currentStopIndex),
                    progressFraction = saved.optDouble("progressFraction", seed.progressFraction.toDouble()).toFloat(),
                    etaMinutes = saved.optInt("etaMinutes", seed.etaMinutes)
                )
            } else seed
        }

        val receiptsArr = root.getJSONArray("receipts")
        val receipts = (0 until receiptsArr.length()).map { idx ->
            val o = receiptsArr.getJSONObject(idx)
            PaymentReceipt(
                receiptNumber = o.getString("receiptNumber"),
                studentId = o.getString("studentId"),
                studentName = o.getString("studentName"),
                grade = o.getString("grade"),
                parentName = o.getString("parentName"),
                tuitionPortionEtb = o.getInt("tuitionPortionEtb"),
                busPortionEtb = o.getInt("busPortionEtb"),
                amountPaidEtb = o.getInt("amountPaidEtb"),
                method = PaymentMethod.valueOf(o.getString("method")),
                referenceNumber = o.getString("referenceNumber"),
                timestamp = o.getString("timestamp"),
                verifiedBy = o.getString("verifiedBy")
            )
        }

        val notifArr = root.getJSONArray("notifications")
        val notifications = (0 until notifArr.length()).map { idx ->
            val o = notifArr.getJSONObject(idx)
            SafetyNotification(
                id = o.getString("id"),
                studentId = o.getString("studentId"),
                studentName = o.getString("studentName"),
                parentId = o.getString("parentId"),
                busCode = o.getString("busCode"),
                titleEn = o.getString("titleEn"),
                titleAm = o.getString("titleAm"),
                detailEn = o.getString("detailEn"),
                detailAm = o.getString("detailAm"),
                timestamp = o.getString("timestamp"),
                type = BoardingStatus.valueOf(o.getString("type"))
            )
        }

        val passesArr = root.getJSONArray("pickupPasses")
        val passes = (0 until passesArr.length()).map { idx ->
            val o = passesArr.getJSONObject(idx)
            val verifiedStr = o.optString("verifiedAt", "")
            PickupPass(
                id = o.getString("id"),
                code = o.getString("code"),
                studentId = o.getString("studentId"),
                studentName = o.getString("studentName"),
                parentId = o.getString("parentId"),
                delegateName = o.getString("delegateName"),
                relationship = o.getString("relationship"),
                delegatePhone = o.getString("delegatePhone"),
                validDate = o.getString("validDate"),
                isVerified = o.optBoolean("isVerified", false),
                verifiedAt = verifiedStr.ifBlank { null }
            )
        }

        return PersistedAppState(
            students = students,
            routes = routes,
            receipts = receipts,
            notifications = notifications,
            pickupPasses = passes,
            activeRole = activeRole,
            language = language,
            selectedParentId = selectedParentId,
            selectedRouteId = selectedRouteId
        )
    }

    companion object {
        private const val KEY_STATE = "app_state_json"
    }
}
