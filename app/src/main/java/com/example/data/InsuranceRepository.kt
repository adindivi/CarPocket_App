package com.example.data

import com.example.model.CommuteChallenge
import com.example.model.InsuranceCompanyQuote
import com.example.model.IssuedCoupon

/**
 * Repository providing UBI insurance special contract discount quotes,
 * commute safety challenges, and initial issued vouchers (Clean Code / SRP).
 */
class InsuranceRepository {

  fun getInsuranceQuotes(): List<InsuranceCompanyQuote> = listOf(
    InsuranceCompanyQuote(
      id = "ins_1",
      companyName = "현대해상 다이렉트",
      badgeText = "최대 16% 할인",
      minScoreRequired = 80,
      maxDiscountRate = 16.0f,
      estimatedAnnualSaving = 196800,
      specialContractName = "안전운전 특약 할인",
      benefitsSummary = "80점만 넘으면 바로 16% 돌려받고, 블랙박스 할인 5%도 함께 받아요"
    ),
    InsuranceCompanyQuote(
      id = "ins_2",
      companyName = "삼성화재 다이렉트 착",
      badgeText = "최대 15.3% 할인",
      minScoreRequired = 81,
      maxDiscountRate = 15.3f,
      estimatedAnnualSaving = 188200,
      specialContractName = "착한 드라이브 할인",
      benefitsSummary = "최근 달린 점수가 81점 이상이면 보험료를 즉시 깎아드려요"
    ),
    InsuranceCompanyQuote(
      id = "ins_3",
      companyName = "DB손해보험 프로미",
      badgeText = "최대 15.0% 할인",
      minScoreRequired = 71,
      maxDiscountRate = 15.0f,
      estimatedAnnualSaving = 184500,
      specialContractName = "안전운전 특약",
      benefitsSummary = "71점만 넘어도 11% 할인, 85점 넘으면 최대 15% 아껴요"
    ),
    InsuranceCompanyQuote(
      id = "ins_4",
      companyName = "KB손해보험 매직카",
      badgeText = "최대 14.8% 할인",
      minScoreRequired = 75,
      maxDiscountRate = 14.8f,
      estimatedAnnualSaving = 182000,
      specialContractName = "안전운전 연계 할인",
      benefitsSummary = "주행거리 할인까지 더하면 1년에 최대 42만원 아낄 수 있어요"
    )
  )

  fun getDefaultCommuteChallenges(): List<CommuteChallenge> = listOf(
    CommuteChallenge(
      id = "chal_1",
      title = "이번 주 출퇴근 무사고 5일",
      subtitle = "월~금 출퇴근 주행 시 급조작 0건 달성 (5/5 완료)",
      rewardTitle = "GS칼텍스 3,000원 주유 할인권",
      rewardTag = "주유 할인",
      iconEmoji = "⛽",
      currentProgress = 5,
      targetProgress = 5,
      periodLabel = "이번 주 월~금",
      isCompleted = true,
      isClaimed = false
    ),
    CommuteChallenge(
      id = "chal_2",
      title = "어린이보호구역 안전속도 100% 준수",
      subtitle = "스쿨존 30km 이하 5회 안전 통과 완료",
      rewardTitle = "SK에너지 자동세차 1회 무료 이용권",
      rewardTag = "세차 쿠폰",
      iconEmoji = "🧼",
      currentProgress = 5,
      targetProgress = 5,
      periodLabel = "11월 월간 미션",
      isCompleted = true,
      isClaimed = false
    ),
    CommuteChallenge(
      id = "chal_3",
      title = "에코 드라이빙 커피값 절약 (3,800원)",
      subtitle = "부드러운 주행으로 3,800원 절약 달성",
      rewardTitle = "스타벅스 아메리카노 모바일 기프티콘",
      rewardTag = "카페 음료",
      iconEmoji = "☕",
      currentProgress = 3800,
      targetProgress = 3800,
      periodLabel = "상시 챌린지",
      isCompleted = true,
      isClaimed = false
    ),
    CommuteChallenge(
      id = "chal_4",
      title = "출퇴근 급감속·급출발 제로 데이",
      subtitle = "오늘 주행 시 급가속 및 급제동 0회 달성",
      rewardTitle = "전기차 급속 충전 5,000원 크레딧",
      rewardTag = "충전 크레딧",
      iconEmoji = "⚡",
      currentProgress = 1,
      targetProgress = 1,
      periodLabel = "오늘 데일리",
      isCompleted = true,
      isClaimed = false
    )
  )

  fun getInitialIssuedCoupons(): List<IssuedCoupon> = listOf(
    IssuedCoupon(
      id = "cpn_welcome",
      title = "신규 가입 웰컴 모바일 주유권",
      brand = "GS칼텍스",
      barcodeNumber = "8801234-987654",
      expiryDate = "2026.12.31까지",
      valueLabel = "2,000원",
      iconEmoji = "⛽",
      isUsed = false
    )
  )
}
