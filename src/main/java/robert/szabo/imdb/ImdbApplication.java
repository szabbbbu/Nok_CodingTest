package robert.szabo.imdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import robert.szabo.imdb.services.MovieQueryBuilder;
import robert.szabo.imdb.services.PersonQueryBuilder;
import robert.szabo.imdb.shell.ShellController;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "robert.szabo.imdb.repository")
public class ImdbApplication {

	public static void main(String[] args) {
		var ctx = SpringApplication.run(ImdbApplication.class, args);
		MovieQueryBuilder mqb = ctx.getBean(MovieQueryBuilder.class);
		PersonQueryBuilder pqb = ctx.getBean(PersonQueryBuilder.class);

		ShellController sc = new ShellController(mqb, pqb);
		sc.execute();
	}

}
