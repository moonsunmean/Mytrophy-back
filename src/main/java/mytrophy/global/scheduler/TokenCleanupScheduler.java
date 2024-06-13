package mytrophy.global.scheduler;

import mytrophy.api.member.entity.RefreshEntity;
import mytrophy.api.member.repository.RefreshRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component
public class TokenCleanupScheduler {

    @Autowired
    private RefreshRepository refreshRepository;

    // 매 정각마다 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredTokens() {
        List<RefreshEntity> tokens = refreshRepository.findAll();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

        for (RefreshEntity token : tokens) {
            try {
                // Date 객체로 파싱
                Date expirationDate = formatter.parse(token.getExpiration());
                // Date 객체를 ZonedDateTime으로 변환
                ZonedDateTime zonedDateTime = expirationDate.toInstant().atZone(ZoneId.systemDefault());
                LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

                if (localDateTime.isBefore(LocalDateTime.now())) {
                    refreshRepository.delete(token);
                }
            } catch (ParseException e) {
                System.err.println("Failed to parse date: " + token.getExpiration());
                e.printStackTrace();
            }
        }
    }
}
