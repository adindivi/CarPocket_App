package com.example.data

import com.example.model.CameraType
import com.example.model.EnforcementCamera

/**
 * Repository providing CCTV & Speed Camera data (SRP).
 */
class CctvRepository {

  fun getDefaultCameras(): List<EnforcementCamera> = listOf(
    EnforcementCamera(
      id = "cam_1",
      title = "역삼역 사거리 (테헤란로)",
      type = CameraType.SPEED_SIGNAL,
      speedLimit = 50,
      distanceMeters = 320,
      roadName = "테헤란로 152",
      activeNotice = "신호위반 및 시속 50km 초과 집중 단속",
      fineNotice = "과속 적발 시 벌점 15점 및 범칙금 60,000원",
      latitude = 37.5006,
      longitude = 127.0364
    ),
    EnforcementCamera(
      id = "cam_2",
      title = "강남역 1번 출구 앞 (강남대로)",
      type = CameraType.BUS_ONLY_LANE,
      speedLimit = 50,
      distanceMeters = 780,
      roadName = "강남대로 396",
      activeNotice = "중앙 버스전용차로 24시간 실시간 무인단속",
      fineNotice = "전용차로 위반 과태료 50,000원 부과",
      latitude = 37.4979,
      longitude = 127.0276
    ),
    EnforcementCamera(
      id = "cam_3",
      title = "선릉역 교차로 (선릉로)",
      type = CameraType.PARKING_ENFORCEMENT,
      speedLimit = 0,
      distanceMeters = 950,
      roadName = "선릉로 432",
      activeNotice = "불법 주정차 5분 초과 시 즉시 자동 과태료",
      fineNotice = "주정차 금지구역 위반 과태료 40,000원",
      latitude = 37.5045,
      longitude = 127.0490
    ),
    EnforcementCamera(
      id = "cam_4",
      title = "올림픽대로 종합운동장 분기점",
      type = CameraType.SECTION_CONTROL,
      speedLimit = 80,
      distanceMeters = 1200,
      roadName = "올림픽대로 14.2km 지점",
      activeNotice = "시점-종점 평균속도 80km/h 단속",
      fineNotice = "구간 초과 시 최대 130,000원 과태료",
      latitude = 37.5061,
      longitude = 127.0398
    ),
    EnforcementCamera(
      id = "cam_5",
      title = "언주역 사거리 어린이보호구역",
      type = CameraType.SPEED_SIGNAL,
      speedLimit = 30,
      distanceMeters = 410,
      roadName = "봉은사로 214",
      activeNotice = "어린이보호구역 시속 30km 및 주정차 절대 금지",
      fineNotice = "어린이보호구역 위반 과태료 2배 (최대 160,000원)",
      latitude = 37.4970,
      longitude = 127.0346
    )
  )
}
