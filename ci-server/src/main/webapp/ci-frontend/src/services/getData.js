// Import the necessary modules
const axios = require('axios');

// Define the function to send the request
async function getData() {
  try {
    // Make the API request to the CI server
    const response = await axios.get('https://your-ci-server.com/api/data');

    // Extract the repository, run log, and result from the response
    const repository = response.data.repository;
    const log = response.data.log;
    const result = response.data.result;

    return { repository, log, result };
  } catch (error) {
    // Handle any errors that occur during the request
    console.error('Error fetching data:', error);
    throw error;
  }
}

// Export the function for use in other modules
module.exports = getData;
