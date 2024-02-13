<template>
  <header>
    <v-toolbar color="primary" dark>
      <v-toolbar-title>CI-Server</v-toolbar-title>
      <v-spacer></v-spacer>
      <div id="app">
        <img src="./assets/logo.png" alt="Vue logo" width="50" height="50" />
      </div>
    </v-toolbar>
  </header>

  <v-container>
    <v-row>
      <v-col cols="12">
        <div class="d-flex justify-center">
          <v-select
            :items="repositories"
            label="Repo"
            v-model="selectedRepo"
            @click="getRepo"
          ></v-select>
          <v-select
            :items="branches"
            label="Branch"
            v-model="selectedBranch"
            @click="getBranch"
          ></v-select>
          <v-select
            :items="commits"
            label="Commit"
            v-model="selectedCommit"
            @click="getCommit"
            @update:model-value="showData"
          ></v-select>
        </div>
      </v-col>
    </v-row>
  </v-container>

  <v-container>
    <v-row>
      <v-col cols="12">
        <div class="d-flex flex-wrap ga-2">
          <v-chip
            :prepend-icon="
              compilePassed == 0
                ? 'mdi-alert'
                : compilePassed == 1
                ? 'mdi-checkbox-marked-circle'
                : 'mdi-close-circle'
            "
            :color="
              compilePassed == 0 ? 'grey' : compilePassed == 1 ? 'green' : 'red'
            "
          >
            Compile
          </v-chip>
          <v-chip
            :prepend-icon="
              testPassed == 0
                ? 'mdi-alert'
                : testPassed == 1
                ? 'mdi-checkbox-marked-circle'
                : 'mdi-close-circle'
            "
            :color="
              testPassed == 0 ? 'grey' : testPassed == 1 ? 'green' : 'red'
            "
          >
            Test
          </v-chip>
        </div>
      </v-col>
      <v-textarea
        :readonly="isReadOnly"
        label="Log"
        outlined
        rows="10"
        v-model="log"
      ></v-textarea>
    </v-row>
  </v-container>
</template>
<script>
// import the serverUrl from URL config
import { serverUrl } from "../../URLconfig";

import axios from "axios";
export default {
  data: () => ({
    commits: [
      // "commit1",
    ],
    commitsData: null,
    selectedCommit: "",
    branches: [
      //  "bugfix",
    ],
    selectedBranch: "",
    repositories: [
      // "repo1",
    ],
    selectedRepo: "",
    isReadOnly: true,
    log: "",
    compilePassed: 0, // 0: not tested, 1: passed, 2: failed
    testPassed: 0, // 0: not tested, 1: passed, 2: failed
    post: null,
    // API_URL: "http://localhost:8080/ci/api/",
    API_URL: `${serverUrl}/ci/api/`,
    //    API_URL: "https://formally-quick-krill.ngrok-free.app/ci/api/",
  }),

  created() {
    this.fetchData();
  },

  watch: { $route: "fetchData" },
  methods: {
    fetchData() {
      this.selectedBranch = this.$route.query.branch;
      this.selectedRepo = this.$route.query.repo;
      this.selectedCommit = this.$route.query.commit;
      if (
        typeof this.selectedBranch !== "undefined" &&
        typeof this.selectedRepo !== "undefined" &&
        typeof this.selectedCommit !== "undefined"
      ) {
        this.fetch();
      }
    },
    showData() {
      for (let i = 0; i < this.commitsData.commits.length; i++) {
        if (
          this.selectedCommit + ".json" ===
          this.commitsData.commits[i].name
        ) {
          this.log = this.post.data.commits[i].log;
          this.compilePassed = this.post.data.commits[i].compilePass;
          this.testPassed = this.post.data.commits[i].testPass;
        }
      }
    },
    async fetch() {
      this.post = await axios.get(
        this.API_URL +
          "commit/?repo=" +
          this.selectedRepo +
          "&branch=" +
          this.selectedBranch
      );
      // remove .json
      for (let i = 0; i < this.post.data.commits.length; i++) {
        this.commits[i] = this.post.data.commits[i].name.slice(0, -5);
      }
      this.showData();
    },
    async getRepo() {
      this.post = await axios.get(this.API_URL + "repo");
      this.repositories = this.post.data.repos;
    },

    async getBranch() {
      this.post = await axios.get(
        this.API_URL + "branch/?repo=" + this.selectedRepo
      );
      this.branches = this.post.data.branches;
    },

    async getCommit() {
      this.post = await axios.get(
        this.API_URL +
          "commit/?repo=" +
          this.selectedRepo +
          "&branch=" +
          this.selectedBranch
      );
      this.commitsData = this.post.data;
      // remove .json
      for (let i = 0; i < this.post.data.commits.length; i++) {
        this.commits[i] = this.post.data.commits[i].name.slice(0, -5);
      }
    },
  },
};
</script>
