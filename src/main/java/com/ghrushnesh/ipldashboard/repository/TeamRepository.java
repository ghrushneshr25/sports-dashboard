package com.ghrushnesh.ipldashboard.repository;

import org.springframework.data.repository.CrudRepository;
import com.ghrushnesh.ipldashboard.model.Team;

public interface TeamRepository extends CrudRepository<Team, Long> {

    Team findByTeamName(String teamName);

}
