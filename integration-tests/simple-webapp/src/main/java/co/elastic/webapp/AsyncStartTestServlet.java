/*
 * Licensed to Elasticsearch B.V. under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch B.V. licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package co.elastic.webapp;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static co.elastic.webapp.Constants.CAUSE_DB_ERROR;
import static co.elastic.webapp.Constants.CAUSE_TRANSACTION_ERROR;
import static co.elastic.webapp.Constants.TRANSACTION_FAILURE;

public class AsyncStartTestServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        final AsyncContext ctx = req.startAsync();
        ctx.setTimeout(500);
        final boolean causeDbError = req.getParameter(CAUSE_DB_ERROR) != null;
        final boolean causeTransactionError = req.getParameter(CAUSE_TRANSACTION_ERROR) != null;
        ctx.start(new Runnable() {
            @Override
            public void run() {
                Exception cause = null;
                String content;
                try {
                    content = TestDAO.instance().queryDb(causeDbError);
                } catch (SQLException e) {
                    cause = e;
                    content = Constants.DB_ERROR;
                }
                try {
                    ctx.getResponse().getWriter().append(content);
                } catch (Exception e) {
                    cause = e;
                }
                if (causeTransactionError) {
                    throw new RuntimeException(TRANSACTION_FAILURE, cause);
                }
                ctx.complete();
            }
        });
    }
}
