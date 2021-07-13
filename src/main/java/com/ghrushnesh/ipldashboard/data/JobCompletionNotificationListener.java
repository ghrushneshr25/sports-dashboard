package com.ghrushnesh.ipldashboard.data;

import java.util.HashMap;
import java.util.Map;
import com.ghrushnesh.ipldashboard.model.Team;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final EntityManager em; // JPA Way of interacting with Database

    @Autowired
    public JobCompletionNotificationListener(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            Map<String, Team> teamData = new HashMap<String, Team>();

            em.createQuery("SELECT m.team1, count(*) from Match m group by m.team1", Object[].class).getResultList()
                    .stream().map(e -> new Team((String) e[0], (long) e[1]))
                    .forEach(team -> teamData.put(team.getTeamName(), team));

            // JPA will create query individaual team and games played , each row will be
            // object array , then it will map to team instance then populate the team table
            // with details.

            em.createQuery("SELECT m.team2, count(*) from Match m group by m.team2", Object[].class).getResultList()
                    .stream().forEach(e -> {
                        Team team = teamData.get((String) e[0]);
                        team.setTotalMatches(team.getTotalMatches() + (long) e[1]);
                    });

            em.createQuery("SELECT m.matchWinner, count(*) from Match m group by m.matchWinner", Object[].class)
                    .getResultList().stream().forEach(e -> {
                        Team team = teamData.get((String) e[0]);
                        if (team != null)
                            team.setTotalWins((long) e[1]);
                    });

            teamData.values().forEach(team -> em.persist(team));
            teamData.values().forEach(team -> System.out.println(team));
        }
    }
}
