<template>
  <v-container>
    <v-row class="text-center">
      <v-col cols="12"> </v-col>
      <v-col class="mb-4" cols="8">
        <h1 class="display-2 font-weight-bold mb-3" style="color: #0172bf">
          Todo APP
        </h1>
        <center v-if="todoList.length === 0">
          <v-img src="@/assets/data-not-found.png" width="190" />
          <p>Not found Any todo list TwT</p>
        </center>
        <center v-else>
          <v-container>
          <v-row v-for="(todo,i) in todoList" :key="i"> 
          <v-card elevation="16" max-width="400" class="mx-auto" style="border-radius:5%">
            <v-list-item three-line>
              <v-list-item-content>
                <v-list-item-title class="headline mb-1">
                  {{i+1}}. {{todo.todoType}}
                </v-list-item-title>
                <v-list-item-subtitle>
                  {{todo.todoDetail}}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
            <!-- <v-virtual-scroll
              :bench="benched"
              :items="items"
              height="300"
              item-height="64"
            >
              <template v-slot:default="{ item }">
                <v-list-item :key="item">
                  <v-list-item-action>
                    <v-btn fab small depressed color="primary">
                      {{ item }}
                    </v-btn>
                  </v-list-item-action>

                  <v-list-item-content>
                    <v-list-item-title>
                      User Database Record <strong>ID {{ item }}</strong>
                    </v-list-item-title>
                  </v-list-item-content>

                  <v-list-item-action>
                    <v-icon small> mdi-open-in-new </v-icon>
                  </v-list-item-action>
                </v-list-item>

                <v-divider></v-divider>
              </template>
            </v-virtual-scroll> -->
          </v-card>
          </v-row>
          </v-container>
        </center>
      </v-col>
      <v-col>
        <v-row>
          <v-form>
            <v-select
              :items="noteTypePreset"
              label="Note Type"
              v-model="todoType"
            ></v-select>
            <v-textarea
              v-model="todoDetail"
              label="Description"
              required
            ></v-textarea>
            <v-btn
              style="color: white"
              color="#0172bf"
              class="mr-4"
              @click="createTodo()"
            >
              Submit
            </v-btn>
          </v-form>
        </v-row>
        <v-row> </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import axios from "axios";
export default {
  name: "HelloWorld",
  data: () => ({
    todoDetail: null,
    todoType: null,
    todoList: []
    // [
    //   { todoDetail: "Quarkus is Future of cloud Native Development", todoType: "improvement" },
    //   { todoDetail: "My Laptop have low memory it amy cause application crash", todoType: "urgent" }
    // ],
    ,
    noteTypePreset: ["urgent", "review", "improvement"],
    ecosystem: [
      {
        text: "vuetify-loader",
        href: "https://github.com/vuetifyjs/vuetify-loader",
      },
      {
        text: "github",
        href: "https://github.com/vuetifyjs/vuetify",
      },
      {
        text: "awesome-vuetify",
        href: "https://github.com/vuetifyjs/awesome-vuetify",
      },
    ],
  }),
  mounted() {
    this.getAllTodoList();
  },
  methods: {
    getAllTodoList() {
      axios
        .get(`${process.env.VUE_APP_BACKEND}/api/todolist`)
        .then((publicationList) => {
          console.log(publicationList.data);
          this.todoList = publicationList.data;
        });
    },
    createTodo() {
      console.log(this.todoType);
      console.log(this.todoDetail);
      let todoType= this.todoType
      let todoDetail= this.todoDetail
      let todo = {
        todoType,
        todoDetail
      }
      axios
        .post(`${process.env.VUE_APP_BACKEND}/api/todo`,todo)
        .then((publicationList) => {
          console.log(publicationList);
        });
        this.getAllTodoList()
    },
  },
};
</script>
