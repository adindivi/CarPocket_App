package com.example.data

import com.example.model.CameraType
import com.example.model.EnforcementCamera

/**
 * Repository providing Illegal Parking & Stopping Enforcement Camera Data (주정차 단속 구역 & CCTV).
 * Dedicated to preventing parking fines (40,000 ~ 120,000 won) and towing risks.
 */
class CctvRepository {

  fun getDefaultCameras(): List<EnforcementCamera> = listOf(
    EnforcementCamera(
      id = "cam_1",
      title = "선릉역 1번 출구 앞 (선릉로)",
      type = CameraType.PARKING_ENFORCEMENT,
      speedLimit = 0,
      distanceMeters = 320,
      roadName = "선릉로 432",
      activeNotice = "고정식 CCTV 무인단속 (5분 초과 주정차 시 자동 과태료 부과)",
      fineNotice = "일반도로 주정차 위반 과태료 40,000원",
      latitude = 37.5045,
      longitude = 127.0490
    ),
    EnforcementCamera(
      id = "cam_2",
      title = "역삼초등학교 정문 (어린이보호구역)",
      type = CameraType.CHILD_ZONE_PARKING,
      speedLimit = 0,
      distanceMeters = 450,
      roadName = "역삼로 17길 22",
      activeNotice = "어린이보호구역 주정차 절대금지 (정차 즉시 과태료 부과)",
      fineNotice = "어린이보호구역 과태료 3배 적용 (승용차 120,000원)",
      latitude = 37.4970,
      longitude = 127.0346
    ),
    EnforcementCamera(
      id = "cam_3",
      title = "테헤란로 현대백화점 앞 (황색복선)",
      type = CameraType.IMMEDIATE_TOW,
      speedLimit = 0,
      distanceMeters = 680,
      roadName = "테헤란로 517",
      activeNotice = "황색 복선구역 · 불법 주정차 적발 시 즉시 견인 조치",
      fineNotice = "과태료 80,000원 + 견인료 40,000원 별도 부과",
      latitude = 37.5006,
      longitude = 127.0364
    ),
    EnforcementCamera(
      id = "cam_4",
      title = "강남역 11번 출구 소화전 주변",
      type = CameraType.FIRE_HYDRANT,
      speedLimit = 0,
      distanceMeters = 850,
      roadName = "강남대로 406",
      activeNotice = "소화전 5m 이내 · 안전신문고 주민신고 1분 즉시단속",
      fineNotice = "소방시설 주변 주정차 위반 과태료 80,000원",
      latitude = 37.4979,
      longitude = 127.0276
    ),
    EnforcementCamera(
      id = "cam_5",
      title = "신사동 가로수길 상가거리",
      type = CameraType.LUNCH_GRACE,
      speedLimit = 0,
      distanceMeters = 1100,
      roadName = "압구정로 12길 34",
      activeNotice = "점심시간 유예구간 (11:30~14:00 주정차 탄력 허용)",
      fineNotice = "유예시간 외 불법 주정차 시 과태료 40,000원",
      latitude = 37.5061,
      longitude = 127.0398
    )
  )
}
