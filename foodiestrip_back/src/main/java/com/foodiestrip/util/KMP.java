package com.foodiestrip.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.foodiestrip.model.dao.SpamDao;
import com.foodiestrip.model.dto.KMPDto;

@Component
public class KMP {
	private List<KMPDto> list = new ArrayList<>();
	private final SpamDao spamDao;

	private KMP(SpamDao spamDao) {
		// db에서 패턴 테이블 가져오고 arr 크기 세팅
//		List<String> spamList = new ArrayList<>();
//		spamList.add("fuck");
//		spamList.add("시발");
//		spamList.add("병신");
//		spamList.add("개새끼");

		this.spamDao = spamDao;
		List<String> spamList = spamDao.spamList();

		for (String spam : spamList) {
			makeTable(spam);
		}
		System.out.println("스팸 리스트 사이즈 : " + spamList.size());
	}

	private void makeTable(String spam) {
		// pi 배열 채우기
		int spamLen = spam.length();
		int pi[] = new int[spamLen];

		int j = 0;
		for (int i = 1; i < spamLen; i++) {
			while (j > 0 && spam.charAt(j) != spam.charAt(i)) {
				j = pi[j - 1];
			}

			if (spam.charAt(j) == spam.charAt(i)) {
				pi[i] = ++j;
			}
		}

		KMPDto kmpDto = new KMPDto();
		kmpDto.setSpam(spam);
		kmpDto.setSpamLen(spamLen);
		kmpDto.setPi(pi);
		list.add(kmpDto);
	}

	public String findSpam(String userName) {
		int userNameLen = userName.length();

		for (KMPDto kmpDto : list) {
			String spam = kmpDto.getSpam();
			int spamLen = kmpDto.getSpamLen();
			int pi[] = kmpDto.getPi();

			int j = 0;
			for (int i = 0; i < userNameLen; i++) {
				while (j > 0 && userName.charAt(i) != spam.charAt(j)) {
					j = pi[j - 1];
				}

				if (userName.charAt(i) == spam.charAt(j)) {
					if (++j == spamLen) {
						// sb.append(userName.substring(0, i - j +
						// 1)).append("[").append(spam).append("]").append(userName.substring(i - j +
						// 1));
						StringBuilder sb = new StringBuilder();
						sb.append("\"").append(userName).append("\"의 [").append(spam).append("]은 사용할 수 없습니다.");

						return sb.toString();
					}
				}
			}
			System.out.println(spam + "통과!!");
		}

		return null;
	}
}

//public class KMPTest {
//	public static void main(String[] args) {
//		KMP kmp = new KMP();
//		String userName = "안녕하세요fuck";
//		
//		List<String> spamList = new ArrayList<>();
//		spamList.add("fuck");
//		spamList.add("시발");
//		spamList.add("병신");
//		spamList.add("개새끼");
//		
//		for (String spam : spamList) {
//			kmp.makeTable(spam);
//			String result = kmp.findSpam(userName);
//			
////			if (!"".equals(result)) {
////				System.out.println("떙!");
////			}
//			
//			if ("".equals(result)) {
//				System.out.println("사용 가능! -> (통과 패턴 : " + spam + ")");
//				
//			} else {
//				System.out.println("%s".formatted(result));
//				break;
//			}
//		}
//	}
//}