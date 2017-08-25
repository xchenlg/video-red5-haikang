package chen.config;

import java.io.File;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务
 * 
 * @author Administrator
 *
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

	protected static Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

	@Scheduled(cron = "0 0 0/10 * * ?") // 每2000秒执行一次
	public void scheduler() {
		logger.info(">>>>>>>>>定时任务 删除昨天之前的旧数据。。。");

		File f = new File(
				SchedulingConfig.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
						+ "files\\");
		if (!f.exists()) {
			f.mkdirs();
		}

		DateTime yesterday = new DateTime().minusDays(1);
		for (File file : f.listFiles()) {
			DateTime dt = new DateTime(file.lastModified());
			if (dt.isBefore(yesterday)) {
				file.delete();
			}
		}

	}
}