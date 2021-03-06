package com.org.zhaohui.shared.logics;

import java.util.Set;

import com.google.common.collect.Sets;
import com.org.zhaohui.shared.basics.Color;
import com.org.zhaohui.shared.basics.Move;
import com.org.zhaohui.shared.basics.Piece;
import com.org.zhaohui.shared.basics.PieceKind;
import com.org.zhaohui.shared.basics.Position;
import com.org.zhaohui.shared.basics.State;

public class StateExplorerImpl implements StateExplorer {

  private static StateExplorerImpl instance;

  private StateExplorerImpl() {
  }

  public static StateExplorerImpl getInstance() {
    if (instance == null) {
      synchronized (StateChangerImpl.class) {
        if (instance == null) {
          return new StateExplorerImpl();
        }
      }
    }
    return instance;
  }

  @Override
  public Set<Move> getPossibleMoves(State state) {
    Set<Move> moves = Sets.newHashSet();
    for (int c = 0; c < 8; c++) {
      for (int r = 0; r < 8; r++) {
        Position pos = new Position(r, c);
        if (state.getPiece(pos) != null
            && state.getPiece(pos).getColor() == state.getTurn()) {
          if (getPossibleMovesFromPosition(state, pos) != null) {
            moves.addAll(getPossibleMovesFromPosition(state, pos));
          }
        }
      }
    }
    return moves;
  }

  @Override
  public Set<Move> getPossibleMovesFromPosition(State state, Position start) {
    Set<Move> moves = Sets.newHashSet();
    if (start == null || state == null) {
      return moves;
    }
    if (state.getPiece(start) == null) {
      return moves;
    }
    Color color = state.getPiece(start).getColor();
    if (color != state.getTurn()) {
      return moves;
    }
    if (state.getGameResult() != null) {
      return moves;
    }

    PieceKind pieceKind = state.getPiece(start).getKind();
    switch (pieceKind) {
    case PAWN:
      moves.addAll(getPossibleMovesFromPawnPosition(state, start));
      break;
    case KNIGHT:
      moves.addAll(getPossibleMovesFromKnightPosition(state, start));
      break;
    case ROOK:
      moves.addAll(getPossibleMovesFromRookPosition(state, start));
      break;
    case BISHOP:
      moves.addAll(getPossibleMovesFromBishopPosition(state, start));
      break;
    case QUEEN:
      moves.addAll(getPossibleMovesFromQueenPosition(state, start));
      break;
    case KING:
      moves.addAll(getPossibleMovesFromKingPosition(state, start));
      break;
    default:
      break;
    }
    Set<Move> tmpmoves = Sets.newHashSet();
    tmpmoves.addAll(moves);
    for (Move move : tmpmoves) {
      if (isIllegalMove(state, move)) {
        moves.remove(move);
      }
    }
    return moves;
  }

  @Override
  public Set<Position> getPossibleStartPositions(State state) {
    Set<Position> positions = Sets.newHashSet();
    for (int c = 0; c < 8; c++) {
      for (int r = 0; r < 8; r++) {
        Position pos = new Position(r, c);
        if (state.getPiece(pos) != null
            && state.getPiece(pos).getColor() == state.getTurn()) {
          if (getPossibleMovesFromPosition(state, pos) != null
              && !getPossibleMovesFromPosition(state, pos).isEmpty()) {
            positions.add(pos);
          }
        }
      }
    }
    return positions;
  }

  private Set<Move> getPossibleMovesFromPawnPosition(State state, Position start) {
    Set<Move> moves = Sets.newHashSet();
    Piece pawn = state.getPiece(start);
    Color color = pawn.getColor();
    int row = start.getRow();
    int col = start.getCol();
    if (color == Color.WHITE && row < 7) {
      if (row == 1) {
        if (state.getPiece(row + 1, col) == null
            && state.getPiece(row + 2, col) == null) {
          moves.add(new Move(start, new Position(row + 2, col), null));
        }
      }
      if (state.getPiece(row + 1, col) == null) {
        if (row == 6) {
          moves
              .add(new Move(start, new Position(row + 1, col), PieceKind.QUEEN));
          moves
              .add(new Move(start, new Position(row + 1, col), PieceKind.ROOK));
          moves.add(new Move(start, new Position(row + 1, col),
              PieceKind.BISHOP));
          moves.add(new Move(start, new Position(row + 1, col),
              PieceKind.KNIGHT));
        } else {
          moves.add(new Move(start, new Position(row + 1, col), null));
        }

      }
      if ((col > 0 && state.getPiece(row + 1, col - 1) != null && state
          .getPiece(row + 1, col - 1).getColor() == Color.BLACK)
          || (col > 0 && state.getEnpassantPosition() != null
              && state.getEnpassantPosition().getRow() == row
              && state.getEnpassantPosition().getCol() == col - 1 && state
              .getPiece(state.getEnpassantPosition()).getColor() == Color.BLACK)) {
        if (row == 6) {
          moves.add(new Move(start, new Position(row + 1, col - 1),
              PieceKind.QUEEN));
          moves.add(new Move(start, new Position(row + 1, col - 1),
              PieceKind.ROOK));
          moves.add(new Move(start, new Position(row + 1, col - 1),
              PieceKind.BISHOP));
          moves.add(new Move(start, new Position(row + 1, col - 1),
              PieceKind.KNIGHT));
        } else {
          moves.add(new Move(start, new Position(row + 1, col - 1), null));
        }

      }
      if ((col < 7 && state.getPiece(row + 1, col + 1) != null && state
          .getPiece(row + 1, col + 1).getColor() == Color.BLACK)
          || (col < 7 && state.getEnpassantPosition() != null
              && state.getEnpassantPosition().getRow() == row
              && state.getEnpassantPosition().getCol() == col + 1 && state
              .getPiece(state.getEnpassantPosition()).getColor() == Color.BLACK)) {
        if (row == 6) {
          moves.add(new Move(start, new Position(row + 1, col + 1),
              PieceKind.QUEEN));
          moves.add(new Move(start, new Position(row + 1, col + 1),
              PieceKind.ROOK));
          moves.add(new Move(start, new Position(row + 1, col + 1),
              PieceKind.BISHOP));
          moves.add(new Move(start, new Position(row + 1, col + 1),
              PieceKind.KNIGHT));
        } else {
          moves.add(new Move(start, new Position(row + 1, col + 1), null));
        }
      }

    } else if ((color == Color.BLACK && row > 0)) {

      if (row == 6) {
        if (state.getPiece(row - 1, col) == null
            && state.getPiece(row - 2, col) == null) {
          moves.add(new Move(start, new Position(row - 2, col), null));
        }
      }
      if (state.getPiece(row - 1, col) == null) {
        if (row == 1) {
          moves
              .add(new Move(start, new Position(row - 1, col), PieceKind.QUEEN));
          moves
              .add(new Move(start, new Position(row - 1, col), PieceKind.ROOK));
          moves.add(new Move(start, new Position(row - 1, col),
              PieceKind.BISHOP));
          moves.add(new Move(start, new Position(row - 1, col),
              PieceKind.KNIGHT));
        } else {
          moves.add(new Move(start, new Position(row - 1, col), null));
        }

      }
      if ((col > 0 && state.getPiece(row - 1, col - 1) != null && state
          .getPiece(row - 1, col - 1).getColor() == Color.WHITE)
          || (col > 0 && state.getEnpassantPosition() != null
              && state.getEnpassantPosition().getRow() == row
              && state.getEnpassantPosition().getCol() == col - 1 && state
              .getPiece(state.getEnpassantPosition()).getColor() == Color.WHITE)) {
        if (row == 1) {
          moves.add(new Move(start, new Position(row - 1, col - 1),
              PieceKind.QUEEN));
          moves.add(new Move(start, new Position(row - 1, col - 1),
              PieceKind.ROOK));
          moves.add(new Move(start, new Position(row - 1, col - 1),
              PieceKind.BISHOP));
          moves.add(new Move(start, new Position(row - 1, col - 1),
              PieceKind.KNIGHT));
        } else {
          moves.add(new Move(start, new Position(row - 1, col - 1), null));
        }

      }
      if ((col < 7 && state.getPiece(row - 1, col + 1) != null && state
          .getPiece(row - 1, col + 1).getColor() == Color.WHITE)
          || (col < 7 && state.getEnpassantPosition() != null
              && state.getEnpassantPosition().getRow() == row
              && state.getEnpassantPosition().getCol() == col + 1 && state
              .getPiece(state.getEnpassantPosition()).getColor() == Color.WHITE)) {
        if (row == 1) {
          moves.add(new Move(start, new Position(row - 1, col + 1),
              PieceKind.QUEEN));
          moves.add(new Move(start, new Position(row - 1, col + 1),
              PieceKind.ROOK));
          moves.add(new Move(start, new Position(row - 1, col + 1),
              PieceKind.BISHOP));
          moves.add(new Move(start, new Position(row - 1, col + 1),
              PieceKind.KNIGHT));
        } else {
          moves.add(new Move(start, new Position(row - 1, col + 1), null));
        }
      }
    }
    return moves;

  }

  private Set<Move> getPossibleMovesFromKnightPosition(State state,
      Position start) {
    Set<Move> moves = Sets.newHashSet();
    Piece knight = state.getPiece(start);
    Color color = knight.getColor();
    int row = start.getRow();
    int col = start.getCol();
    int[] rowDif = new int[] { 2, 2, 1, 1, -1, -1, -2, -2 };
    int[] colDif = new int[] { 1, -1, 2, -2, 2, -2, 1, -1 };
    for (int i = 0; i < 8; i++) {
      int newRow = row + rowDif[i];
      int newCol = col + colDif[i];
      if (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7) {
        Position toPos = new Position(newRow, newCol);
        if (state.getPiece(toPos) == null
            || state.getPiece(toPos).getColor() != color) {
          moves.add(new Move(start, toPos, null));
        }
      }
    }
    return moves;
  }

  private Set<Move> getPossibleMovesFromRookPosition(State state, Position start) {
    Set<Move> moves = Sets.newHashSet();
    Piece rook = state.getPiece(start);
    Color color = rook.getColor();
    int row = start.getRow();
    int col = start.getCol();
    int[] rowDif = { 0, 0, 1, -1 };
    int[] colDif = { 1, -1, 0, 0 };
    for (int i = 0; i < 4; i++) {
      for (int j = 1; j < 8; j++) {
        int toRow = row + rowDif[i] * j;
        int toCol = col + colDif[i] * j;
        if (toRow > 7 || toRow < 0 || toCol > 7 || toCol < 0) {
          break;
        }
        Position toPos = new Position(toRow, toCol);
        if (state.getPiece(toPos) == null) {
          moves.add(new Move(start, toPos, null));
        } else {
          if (state.getPiece(toPos).getColor() != color) {
            moves.add(new Move(start, toPos, null));
          }
          break;
        }
      }
    }

    return moves;

  }

  private Set<Move> getPossibleMovesFromBishopPosition(State state,
      Position start) {
    Set<Move> moves = Sets.newHashSet();
    Piece bishop = state.getPiece(start);
    Color color = bishop.getColor();
    int row = start.getRow();
    int col = start.getCol();
    int[] rowDif = { 1, 1, -1, -1 };
    int[] colDif = { 1, -1, 1, -1 };
    for (int i = 0; i < 4; i++) {
      for (int j = 1; j < 8; j++) {
        int toRow = row + rowDif[i] * j;
        int toCol = col + colDif[i] * j;
        if (toRow > 7 || toRow < 0 || toCol > 7 || toCol < 0) {
          break;
        }
        Position toPos = new Position(toRow, toCol);
        if (state.getPiece(toPos) == null) {
          moves.add(new Move(start, toPos, null));
        } else {
          if (state.getPiece(toPos).getColor() != color) {
            moves.add(new Move(start, toPos, null));
          }
          break;
        }

      }
    }
    return moves;
  }

  private Set<Move> getPossibleMovesFromQueenPosition(State state,
      Position start) {
    Set<Move> moves = Sets.newHashSet();
    moves.addAll(getPossibleMovesFromRookPosition(state, start));
    moves.addAll(getPossibleMovesFromBishopPosition(state, start));
    return moves;

  }

  private Set<Move> getPossibleMovesFromKingPosition(State state, Position start) {
    Set<Move> moves = Sets.newHashSet();
    Piece king = state.getPiece(start);
    Color color = king.getColor();
    int row = start.getRow();
    int col = start.getCol();
    if (state.isCanCastleQueenSide(color) && col == 4 && (row == 0 || row == 7)
        && state.getPiece(row, 1) == null && state.getPiece(row, 2) == null
        && state.getPiece(row, 3) == null && state.getPiece(row, 0) != null
        && state.getPiece(row, 0).getKind() == PieceKind.ROOK
        && !isKingCanBeChecked(state, start)
        && !isKingCanBeChecked(state, new Position(row, 3))) {

      moves.add(new Move(start, new Position(row, 2), null));
    }
    if (state.isCanCastleKingSide(color) && col == 4 && (row == 0 || row == 7)
        && state.getPiece(row, 5) == null && state.getPiece(row, 6) == null
        && state.getPiece(row, 7) != null
        && state.getPiece(row, 7).getKind() == PieceKind.ROOK
        && !isKingCanBeChecked(state, start)
        && !isKingCanBeChecked(state, new Position(row, 5))) {
      moves.add(new Move(start, new Position(row, 6), null));
    }
    int[] rowDif = { 1, 1, 1, 0, 0, -1, -1, -1 };
    int[] colDif = { -1, 0, 1, -1, 1, -1, 0, 1 };
    for (int i = 0; i < 8; i++) {
      int toRow = row + rowDif[i];
      int toCol = col + colDif[i];
      if (toRow >= 0 && toRow <= 7 && toCol >= 0 && toCol <= 7) {
        Position toPos = new Position(toRow, toCol);
        if (state.getPiece(toPos) == null
            || state.getPiece(toPos).getColor() != color) {
          moves.add(new Move(start, toPos, null));
        }
      }
    }
    return moves;

  }

  /*
   * Check whether the move will lead king being checked
   */
  public boolean isIllegalMove(State state, Move move) {
    Position from = move.getFrom();
    Position to = move.getTo();
    Piece movedPiece = state.getPiece(from);
    Color color = movedPiece.getColor();
    State tmp = state.copy();
    // BUG here
    tmp.setPiece(from, null);
    if (move.getPromoteToPiece() == null) {
      tmp.setPiece(to, movedPiece);
    } else {
      tmp.setPiece(to, new Piece(color, move.getPromoteToPiece()));
    }
    Position enpassantPos = tmp.getEnpassantPosition();
    if (movedPiece.getKind() == PieceKind.PAWN && enpassantPos != null
        && tmp.getPiece(enpassantPos) != null
        && tmp.getPiece(enpassantPos).getColor() != color
        && enpassantPos.getRow() == move.getFrom().getRow()
        && enpassantPos.getCol() == move.getTo().getCol()) {
      tmp.setPiece(enpassantPos, null);
    }
    if (movedPiece.getKind() == PieceKind.KING) {
      int colDiff = Math.abs(from.getCol() - to.getCol());
      int rowDiff = Math.abs(from.getRow() - to.getRow());
      if (colDiff == 2 && rowDiff == 0) {
        if (to.getCol() - from.getCol() > 0) {
          tmp.setPiece(new Position(from.getRow(), from.getCol() + 1),
              new Piece(color, PieceKind.ROOK));
          tmp.setPiece(from.getRow(), 7, null);
        } else {
          tmp.setPiece(new Position(from.getRow(), from.getCol() - 1),
              new Piece(color, PieceKind.ROOK));
          tmp.setPiece(from.getRow(), 0, null);
        }
      }
    }

    Position kingPos = null;
    for (int r = 0; r < State.ROWS; r++) {
      for (int c = 0; c < State.COLS; c++) {
        if (tmp.getPiece(r, c) != null
            && tmp.getPiece(r, c).getColor() == color
            && tmp.getPiece(r, c).getKind() == PieceKind.KING) {
          kingPos = new Position(r, c);
        }
      }
    }
    if (kingPos == null) {
      return true;
    }
    for (int r = 0; r < State.ROWS; r++) {
      for (int c = 0; c < State.COLS; c++) {
        Position tmpPos = new Position(r, c);
        if (tmp.getPiece(r, c) != null
            && tmp.getPiece(tmpPos).getColor() != color
            && isCanCheckMate(tmp, tmpPos, kingPos)) {
          return true;
        }
      }

    }

    return false;
  }

  private boolean isKingCanBeChecked(State state, Position kingPos) {
    for (int r = 0; r < State.ROWS; r++) {
      for (int c = 0; c < State.COLS; c++) {
        Position tmpPos = new Position(r, c);
        if (state.getPiece(tmpPos) != null
            && state.getPiece(tmpPos).getColor() != state.getTurn()) {
          if (isCanCheckMate(state, tmpPos, kingPos)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean isCanCheckMate(State state, Position fromPos, Position kingPos) {
    if (state == null || fromPos == null || kingPos == null)
      return false;
    Piece movedPiece = state.getPiece(fromPos);
    if (movedPiece == null)
      return false;
    PieceKind movedPieceKind = movedPiece.getKind();
    int fromRow = fromPos.getRow();
    int fromCol = fromPos.getCol();
    int toRow = kingPos.getRow();
    int toCol = kingPos.getCol();
    int absRowDif = Math.abs(toRow - fromRow);
    int absColDif = Math.abs(toCol - fromCol);
    switch (movedPieceKind) {
    case PAWN:
      return isPawnCanCheckMate(state, fromPos, kingPos);
    case KNIGHT:
      return (absRowDif == 2 && absColDif == 1)
          || (absRowDif == 1 && absColDif == 2);
    case ROOK:
      return isRookCanCheckMate(state, fromPos, kingPos);
    case BISHOP:
      return isBishopCanCheckMate(state, fromPos, kingPos);
    case QUEEN:
      return isRookCanCheckMate(state, fromPos, kingPos)
          || isBishopCanCheckMate(state, fromPos, kingPos);
    case KING:
      return Math.sqrt(absRowDif * absRowDif + absColDif * absColDif) < 2;
    default:
      break;
    }

    return false;
  }

  private boolean isPawnCanCheckMate(State state, Position fromPos,
      Position kingPos) {
    int fromRow = fromPos.getRow();
    int fromCol = fromPos.getCol();
    int toRow = kingPos.getRow();
    int toCol = kingPos.getCol();
    int rowDif = toRow - fromRow;
    int absColDif = Math.abs(toCol - fromCol);
    if (state.getPiece(fromPos).getColor() == Color.BLACK) {
      return rowDif == -1 && absColDif == 1;
    } else {
      return rowDif == 1 && absColDif == 1;
    }
  }

  private boolean isRookCanCheckMate(State state, Position fromPos,
      Position kingPos) {
    int fromRow = fromPos.getRow();
    int fromCol = fromPos.getCol();
    int toRow = kingPos.getRow();
    int toCol = kingPos.getCol();
    int absRowDif = Math.abs(toRow - fromRow);
    int absColDif = Math.abs(toCol - fromCol);
    if (absRowDif == 0) {
      int dif = absColDif / (toCol - fromCol);
      if (dif == 1) {
        for (int i = fromCol + 1; i < toCol; i++) {
          if (state.getPiece(fromRow, i) != null) {
            return false;
          }
        }
        return true;
      } else {
        for (int i = fromCol - 1; i > toCol; i--) {
          if (state.getPiece(fromRow, i) != null) {
            return false;
          }
        }
        return true;
      }
    }

    if (absColDif == 0) {
      int dif = absRowDif / (toRow - fromRow);
      if (dif == 1) {
        for (int i = fromRow + 1; i < toRow; i++) {
          if (state.getPiece(i, fromCol) != null) {
            return false;
          }
        }
        return true;
      } else {
        for (int i = fromRow - 1; i > toRow; i--) {
          if (state.getPiece(i, fromCol) != null) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  private boolean isBishopCanCheckMate(State state, Position fromPos,
      Position kingPos) {
    int fromRow = fromPos.getRow();
    int fromCol = fromPos.getCol();
    int toRow = kingPos.getRow();
    int toCol = kingPos.getCol();
    int absRowDif = Math.abs(toRow - fromRow);
    int absColDif = Math.abs(toCol - fromCol);

    if (absRowDif == absColDif) {
      int rowDiff = (toRow - fromRow) / absRowDif;
      int colDiff = (toCol - fromCol) / absColDif;
      int i = fromRow + rowDiff;
      int j = fromCol + colDiff;
      while (i != toRow && j != toCol) {
        if (state.getPiece(i, j) != null) {
          return false;
        }
        i += rowDiff;
        j += colDiff;
      }
      return true;
    }
    return false;
  }

}
