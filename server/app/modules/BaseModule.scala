package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import daos.UserDAOPostgres
import daos.UserDAO
import daos.ChallengeDAO
import daos.ChallengeDAOPostgres

/**
 * The base Guice module.
 */
class BaseModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  override def configure(): Unit = {
    bind[UserDAO].to[UserDAOPostgres]
    bind[ChallengeDAO].to[ChallengeDAOPostgres]
  }
}
