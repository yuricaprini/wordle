package io.github.yuricaprini.wordleserver.unit;

import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleserver.circle01entities.Ranking;
import io.github.yuricaprini.wordleserver.circle01entities.Score;
import io.github.yuricaprini.wordleserver.circle01entities.Username;

class RankingTest {

  // @Test
  // void testUpdate() {
  //   Ranking ranking = Ranking.getInstance();

  //   Username user1 = new Username("User1");
  //   Username user2 = new Username("User2");

  //   Score initialScore = new Score(100);
  //   Score updatedScore = new Score(150);

  //   ranking.add(user1, initialScore);

  //   // Update the score
  //   ArrayList<Pair<Username, Score>> updatedTop3 =
  //       ranking.update(user1, initialScore, updatedScore);

  //   assertTrue(updatedTop3 != null); // Check if the update was successful

  //   // Verify that the user is no longer in the old top 3
  //   for (Pair<Username, Score> pair : initialTop3) {
  //     assertTrue(!pair.getValue().equals(updatedScore) || !pair.getKey().equals(user1));
  //   }

  //   // Verify that the user is in the new top 3
  //   boolean userInNewTop3 = false;
  //   for (Pair<Username, Score> pair : updatedTop3) {
  //     if (pair.getKey().equals(user1) && pair.getValue().equals(updatedScore)) {
  //       userInNewTop3 = true;
  //       break;
  //     }
  //   }
  //   assertTrue(userInNewTop3);
  // }

  @Test
  void testGetFromUpTo() {
    Ranking ranking = Ranking.getInstance();

    // Add some users with scores
    ranking.add(new Username("User1"), new Score(150));
    ranking.add(new Username("User2"), new Score(120));
    ranking.add(new Username("User3"), new Score(120));
    ranking.add(new Username("User4"), new Score(120));
    ranking.add(new Username("User5"), new Score(120));
    ranking.add(new Username("User6"), new Score(90));

    // Retrieve users with scores greater than 100
    ranking.getBottomUp(10);

    ranking.update(new Username("User3"), new Score(120), new Score(151));

    // Retrieve users with scores greater than 100
    ranking.getBottomUp(new Username("User3"), new Score(120), 10);

    // //Verify the result
    // assertEquals(2, result.size()); // Expecting two users with scores greater than 100
    // assertEquals("User1", result.get(0).getValue0().toString());
    // assertEquals(150, result.get(0).getValue1().getValue());
    // assertEquals("User2", result.get(1).getValue0().toString());
    // assertEquals(120, result.get(1).getValue1().getValue());
  }
}
