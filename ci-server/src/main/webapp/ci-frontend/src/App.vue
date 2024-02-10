<script>
</script>
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
          <v-btn color="primary" onclick="console.log('refreshing...')"
            >Refresh</v-btn
          >
          <v-btn color="primary" onclick="console.log('fetching...')"
            >Fetch</v-btn
          >
        </div>
      </v-row>
    </v-container>
  </div>

  <v-container>
    <v-row>
      <v-col cols="12">
        <div class="d-flex flex-wrap ga-2">
          <v-chip
            prepend-icon="mdi-checkbox-marked-circle"
            variant="outlined"
            color="green"
          >
            Compile
          </v-chip>
          <v-chip prepend-icon="mdi-cancel" variant="outlined" color="red">
            Test
          </v-chip>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>
<script>
export default {
  data: () => ({
    commits: [
      { title: "commit 1" },
      { title: "commit 2" },
      { title: "commit 3" },
      { title: "commit 4" },
    ],
    branches: ["main", "develop", "assessment", "feature"],
    repositories: ["ci-frontend", "ci-backend", "ci-server", "ci-database"],
  }),
};
</script>
