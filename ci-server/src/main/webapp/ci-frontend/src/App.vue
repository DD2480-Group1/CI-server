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
          <v-select :items="repositories" label="Repo"></v-select>
          <v-select :items="branches" label="Branch"></v-select>
          <v-select :items="commits" label="Commit"></v-select>
        </div>
      </v-col>
    </v-row>
  </v-container>

  <div>
    <v-container>
      <v-row>
        <div class="d-flex flex-wrap ga-3">
          <v-btn color="primary" @click="refresh">Refresh</v-btn>
          <v-btn color="primary" @click="fetch">Fetch</v-btn>
        </div>
      </v-row>
    </v-container>
  </div>

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
export default {
  data: () => ({
    commits: [
      // "commit1",
    ],
    branches: [
      //  "bugfix",
    ],
    repositories: [
      // "repo1",
    ],
    isReadOnly: true,
    log: "",
    compilePassed: 0, // 0: not tested, 1: passed, 2: failed
    testPassed: 0, // 0: not tested, 1: passed, 2: failed
  }),

  methods: {
    refresh() {
      console.log("refreshing...");
      // this.compilePassed = (this.compilePassed + 1) % 3;
    },

    fetch() {
      console.log("fetching...");
      // this.testPassed = (this.testPassed + 1) % 3;
    },
  },
};
</script>
