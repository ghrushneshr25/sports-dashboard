import { React, useEffect, useState } from "react";
import { MatchDetailCard } from "../Components/MatchDetailCard";
import { MatchSmallCard } from "../Components/MatchSmallCard";

export const TeamPage = () => {
  const [team, setTeam] = useState({ matches: [] }); // [name , function]

  useEffect(() => {
    const fetchMatches = async () => {
      const response = await fetch(
        "http://localhost:8080/team/Mumbai%20Indians"
      );
      const data = await response.json();

      setTeam(data);
    };
    fetchMatches();
    // Calling in this way bcoz it will make it easier to call async, we cant make useEffect as async.
  }, []);

  return (
    <div className="TeamPage">
      <h1>{team.teamName}</h1>

      <MatchDetailCard match={team.matches[0]} />
      {team.matches.slice(1).map((match) => (
        <MatchSmallCard match={match} />
      ))}
    </div>
  );
};
